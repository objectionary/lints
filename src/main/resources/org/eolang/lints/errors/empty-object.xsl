<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" id="empty-object" version="2.0">
  <xsl:import href="/org/eolang/parser/_funcs.xsl"/>
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="/program/objects//o[not(@base) and not(o) and not(eo:atom(.))]">
        <xsl:element name="defect">
          <xsl:attribute name="line">
            <xsl:value-of select="eo:lineno(@line)"/>
          </xsl:attribute>
          <xsl:attribute name="severity">
            <xsl:text>warning</xsl:text>
          </xsl:attribute>
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
          <xsl:text> is empty. It doesn't have any attributes, neither void nor attached</xsl:text>
        </xsl:element>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
