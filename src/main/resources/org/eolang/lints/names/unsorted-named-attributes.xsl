<?xml version="1.0" encoding="UTF-8"?>
<!--
* SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
* SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" xmlns:xs="http://www.w3.org/2001/XMLSchema" version="2.0" id="unsorted-named-attributes">
  <xsl:import href="/org/eolang/funcs/special-name.xsl"/>
  <xsl:import href="/org/eolang/funcs/test-name.xsl"/>
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <!--
  The "vars-float-up" pass of the parser moves an object named inside an
  argument up into the nearest formation, after the attributes written
  there, and leaves a reference to it, on its line, where it was written.
  A formation written before a pipe "|" moves the same way, and the pipe
  application that follows it gets its name as the base. This key finds
  those references by the formation they belong to.
  -->
  <xsl:key name="left-in-place" match="o[@base and ../@base]" use="for $f in generate-id(ancestor::o[eo:abstract(.)][1]) return (concat($f, ' ', @base, ' ', @line), if (@pipe) then concat($f, ' ', @base, ' |') else ())"/>
  <!--
  TRUE when the parser moved the object up from inside an argument: it is
  not one of the attributes listed in the formation, so it takes no part
  in their order.
  -->
  <xsl:function name="eo:lifted" as="xs:boolean">
    <xsl:param name="o" as="element()"/>
    <xsl:variable name="ref" select="concat(generate-id($o/..), ' ξ.', $o/@name)"/>
    <xsl:sequence select="exists(key('left-in-place', (concat($ref, ' ', $o/@line), concat($ref, ' |')), root($o)))"/>
  </xsl:function>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="//o[eo:abstract(.) and (@local, @name)[1]]/o[(@local, @name)[1] and not(@base = '∅') and not(eo:special((@local, @name)[1])) and not(eo:test-name((@local, @name)[1])) and not(eo:lifted(.))]">
        <xsl:variable name="name" select="(@local, @name)[1]"/>
        <xsl:variable name="previous" select="(preceding-sibling::o[(@local, @name)[1] and not(@base = '∅') and not(eo:special((@local, @name)[1])) and not(eo:test-name((@local, @name)[1])) and not(eo:lifted(.))])[last()]"/>
        <xsl:variable name="prev-name" select="($previous/@local, $previous/@name)[1]"/>
        <xsl:if test="$previous and $name &lt; $prev-name">
          <defect>
            <xsl:variable name="line" select="eo:lineno(@line)"/>
            <xsl:attribute name="line">
              <xsl:value-of select="$line"/>
            </xsl:attribute>
            <xsl:if test="$line = '0'">
              <xsl:attribute name="context">
                <xsl:value-of select="eo:defect-context(.)"/>
              </xsl:attribute>
            </xsl:if>
            <xsl:attribute name="severity">warning</xsl:attribute>
            <xsl:text>Named attribute </xsl:text>
            <xsl:value-of select="eo:escape($name)"/>
            <xsl:text> is out of order inside formation </xsl:text>
            <xsl:value-of select="eo:escape((../@local, ../@name)[1])"/>
          </defect>
        </xsl:if>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
