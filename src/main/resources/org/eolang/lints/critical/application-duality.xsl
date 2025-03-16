<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" id="application-duality" version="2.0">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="//o[@base]">
        <xsl:variable name="args">
          <xsl:choose>
            <xsl:when test="starts-with(@base, '.')">
              <xsl:copy-of select="o[position()&gt;1]"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:copy-of select="o"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
        <xsl:variable name="total" select="count($args/*)"/>
        <xsl:variable name="bindings" select="count($args/*[@as])"/>
        <xsl:variable name="without" select="count($args/*[not(@as)])"/>
        <xsl:if test="$total != $bindings and $total != $without">
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
            <xsl:attribute name="severity">
              <xsl:text>critical</xsl:text>
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
            <xsl:text> cannot mix child elements with and without the @as attribute: </xsl:text>
            <xsl:value-of select="$total"/>
            <xsl:text> children, </xsl:text>
            <xsl:value-of select="$bindings"/>
            <xsl:text> with, </xsl:text>
            <xsl:value-of select="$without"/>
            <xsl:text> without; please ensure that all child elements either have the @as attribute or none of them do</xsl:text>
          </defect>
        </xsl:if>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
