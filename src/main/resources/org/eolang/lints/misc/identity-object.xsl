<?xml version="1.0" encoding="UTF-8"?>
<!--
* SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
* SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" id="identity-object" version="2.0">
  <xsl:import href="/org/eolang/parser/_funcs.xsl"/>
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="//o[eo:abstract(.) and count(o) = 2 and o[1]/@base='∅' and o[1]/@name and not(o[1]/o) and o[2]/@name='φ' and not(o[2]/@const) and not(o[2]/o) and o[2]/@base=concat('ξ.', o[1]/@name) and not(@line = o[1]/@line and @line = o[2]/@line and @pos = o[1]/@pos and @pos = o[2]/@pos)]">
        <xsl:element name="defect">
          <xsl:variable name="line" select="eo:lineno(@line)"/>
          <xsl:attribute name="line">
            <xsl:value-of select="$line"/>
          </xsl:attribute>
          <xsl:if test="$line = '0'">
            <xsl:attribute name="context">
              <xsl:value-of select="eo:defect-context(.)"/>
            </xsl:attribute>
          </xsl:if>
          <xsl:attribute name="severity">
            <xsl:text>warning</xsl:text>
          </xsl:attribute>
          <xsl:attribute name="experimental">true</xsl:attribute>
          <xsl:text>The </xsl:text>
          <xsl:choose>
            <xsl:when test="@name">
              <xsl:text>formation </xsl:text>
              <xsl:value-of select="eo:escape(@name)"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text>anonymous formation</xsl:text>
            </xsl:otherwise>
          </xsl:choose>
          <xsl:text> is a handwritten identity object and may be replaced with the "I" glyph</xsl:text>
        </xsl:element>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
