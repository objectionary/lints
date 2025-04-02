<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" version="2.0" id="duplicate-as-attribute">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:key name="as-key" match="o/@as" use="."/>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="//o[o[@as]]">
        <xsl:variable name="oname" select="@name"/>
        <xsl:variable name="line" select="eo:lineno(@line)"/>
        <xsl:variable name="context" select="eo:defect-context(.)"/>
        <xsl:variable name="attributes" select="o/@as"/>
        <xsl:for-each select="distinct-values($attributes)">
          <xsl:variable name="current" select="."/>
          <xsl:variable name="duplicates" select="count($attributes[.= $current])"/>
          <xsl:if test="$duplicates &gt; 1">
            <defect>
              <xsl:attribute name="line">
                <xsl:value-of select="$line"/>
              </xsl:attribute>
              <xsl:if test="$line = '0'">
                <xsl:attribute name="context">
                  <xsl:value-of select="$context"/>
                </xsl:attribute>
              </xsl:if>
              <xsl:attribute name="severity">warning</xsl:attribute>
              <xsl:text>The </xsl:text>
              <xsl:choose>
                <xsl:when test="$oname">
                  <xsl:text>object </xsl:text>
                  <xsl:value-of select="eo:escape($oname)"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:text>anonymous object</xsl:text>
                </xsl:otherwise>
              </xsl:choose>
              <xsl:text> has duplicated @as attribute </xsl:text>
              <xsl:value-of select="eo:escape($current)"/>
            </defect>
          </xsl:if>
        </xsl:for-each>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
