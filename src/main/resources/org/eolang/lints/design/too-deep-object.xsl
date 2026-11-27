<?xml version="1.0" encoding="UTF-8"?>
<!--
* SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
* SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" version="2.0" id="too-deep-object">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:import href="/org/eolang/funcs/depth.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="//o[not(eo:literal-part(.)) and eo:depth(.) &gt; 12 and not(.//o[not(eo:literal-part(.)) and eo:depth(.) &gt; 12])]">
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
          <xsl:text>The object </xsl:text>
          <xsl:variable name="tail" select="tokenize(@base, '\.')[last()]"/>
          <xsl:value-of select="eo:escape((@local, @name, ancestor::o/o[@name=$tail]/@local, $tail)[string(.) != ''][1])"/>
          <xsl:text> is nested too deep: </xsl:text>
          <xsl:value-of select="eo:depth(.)"/>
          <xsl:text> levels of nesting, while the maximum is 12</xsl:text>
        </defect>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
