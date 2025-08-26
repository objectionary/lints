<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" id="direct-phi" version="2.0">
  <xsl:import href="/org/eolang/parser/_funcs.xsl"/>
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="//o[contains(@base, '.φ')]">
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
            <xsl:text>error</xsl:text>
          </xsl:attribute>
          <xsl:attribute name="experimental">true</xsl:attribute>
          <xsl:text>The </xsl:text>
          <xsl:choose>
            <xsl:when test="@name">
              <xsl:text>object </xsl:text>
              <xsl:value-of select="eo:escape(@name)"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text>anonymous object</xsl:text>
            </xsl:otherwise>
          </xsl:choose>
          <xsl:text> uses "@" attribute directly, while it is prohibited</xsl:text>
        </xsl:element>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
