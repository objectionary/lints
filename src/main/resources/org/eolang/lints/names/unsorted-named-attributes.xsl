<?xml version="1.0" encoding="UTF-8"?>
<!--
* SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
* SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" version="2.0" id="unsorted-named-attributes">
  <xsl:import href="/org/eolang/funcs/special-name.xsl"/>
  <xsl:import href="/org/eolang/funcs/test-name.xsl"/>
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="//o[eo:abstract(.) and (@local, @name)[1]]/o[(@local, @name)[1] and not(@base = '∅') and not(eo:special((@local, @name)[1])) and not(eo:test-name((@local, @name)[1]))]">
        <xsl:variable name="name" select="(@local, @name)[1]"/>
        <xsl:variable name="previous" select="(preceding-sibling::o[(@local, @name)[1] and not(@base = '∅') and not(eo:special((@local, @name)[1])) and not(eo:test-name((@local, @name)[1]))])[last()]"/>
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
