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
  When such an object is the receiver of a vertical method, as in
  "not. >> has-port" right below "if.", no reference is left on its line:
  the parser folds it into the base of the method, "ξ.a🌵6-4.if", on the
  line of the method, right above the object. This key finds those bases
  by the formation they belong to, the object they dispatch on and the
  line right below them.
  -->
  <xsl:key name="received" match="o[matches(@base, '^ξ\.[^.]+\.[^.]+$')]" use="concat(generate-id(ancestor::o[eo:abstract(.)][1]), ' ', replace(@base, '\.[^.]+$', ''), ' ', number(@line) + 1)"/>
  <!--
  TRUE when the parser moved the object up from inside an argument: it is
  not one of the attributes listed in the formation, so it takes no part
  in their order. The other arguments of a method that receives it, if
  any, start below its last line, while those of a method that merely
  uses an attribute declared on the next line start on the line of the
  method.
  -->
  <xsl:function name="eo:lifted" as="xs:boolean">
    <xsl:param name="o" as="element()"/>
    <xsl:variable name="ref" select="concat(generate-id($o/..), ' ξ.', $o/@name)"/>
    <xsl:variable name="last" select="max($o/descendant-or-self::o/number(@line))"/>
    <xsl:sequence select="exists(key('left-in-place', (concat($ref, ' ', $o/@line), concat($ref, ' |')), root($o))) or exists(key('received', concat($ref, ' ', $o/@line), root($o))[every $a in o satisfies number($a/@line) gt $last])"/>
  </xsl:function>
  <!--
  When the next line continues an attribute with a method, as in
  "foo > app" followed by ".bar > baz", the parser reads ".bar" as a
  method of "app": it moves "app" after the attributes of the formation and
  leaves the reference to it in the base of "baz", "ξ.app.bar", which stays
  in the place of the chain. This key finds such bases by the formation
  and the object they dispatch on.
  -->
  <xsl:key name="continuing" match="o[matches(@base, '^ξ\.[^.]+\.') and not(../@base)]" use="concat(generate-id(..), ' ', replace(@base, '^ξ\.([^.]+)\..*$', '$1'))"/>
  <!--
  TRUE when the next line continues the attribute with a method, and the
  parser moved it after the attribute that line names: that one stands for
  the chain in the order, so this one takes no part in it. An attribute
  that merely uses this one elsewhere either stays after it in the XMIR or
  does not start on the line right below it.
  -->
  <xsl:function name="eo:continued" as="xs:boolean">
    <xsl:param name="o" as="element()"/>
    <xsl:sequence select="exists(key('continuing', concat(generate-id($o/..), ' ', $o/@name), root($o))[. &lt;&lt; $o and number(@line) = max($o/descendant-or-self::o/number(@line)) + 1])"/>
  </xsl:function>
  <xsl:template match="/">
    <defects>
      <!--
      A pipe application, such as "| x > name", carries an empty "@pipe" and
      applies the attribute written right above it, so it can never move
      away from there and takes no part in the order of the attributes.
      -->
      <xsl:for-each select="//o[eo:abstract(.) and (@local, @name)[1]]/o[(@local, @name)[1] and not(@base = '∅') and not(@pipe) and not(eo:special((@local, @name)[1])) and not(eo:test-name((@local, @name)[1])) and not(eo:lifted(.)) and not(eo:continued(.))]">
        <xsl:variable name="name" select="(@local, @name)[1]"/>
        <xsl:variable name="previous" select="(preceding-sibling::o[(@local, @name)[1] and not(@base = '∅') and not(@pipe) and not(eo:special((@local, @name)[1])) and not(eo:test-name((@local, @name)[1])) and not(eo:lifted(.)) and not(eo:continued(.))])[last()]"/>
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
