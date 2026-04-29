<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" xmlns:xs="http://www.w3.org/2001/XMLSchema" id="unused-void-attr" version="2.0">
  <xsl:import href="/org/eolang/parser/_funcs.xsl"/>
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="//o[@base='∅']">
        <xsl:variable name="attr" select="@name"/>
        <xsl:variable name="formation" select=".."/>
        <xsl:variable name="pattern" select="concat('^ξ(\.ρ)*\.', $attr, '(\..*)?$')"/>
        <xsl:variable name="usages" as="element()*">
          <xsl:for-each select="$formation//o[matches(@base, $pattern)]">
            <xsl:variable name="rhos" select="replace(@base, concat('^ξ((\.ρ)*)\.', $attr, '.*$'), '$1')"/>
            <xsl:variable name="hops" select="string-length($rhos) - string-length(translate($rhos, 'ρ', ''))" as="xs:integer"/>
            <xsl:variable name="anchor" select="ancestor::o[eo:abstract(.)][position() = $hops + 1]"/>
            <xsl:if test="exists($anchor) and generate-id($anchor) = generate-id($formation)">
              <xsl:sequence select="."/>
            </xsl:if>
          </xsl:for-each>
        </xsl:variable>
        <xsl:if test="not($attr='φ') and not(eo:atom($formation)) and empty($usages) and not($formation//o[eo:atom(.)])">
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
            <xsl:text>The void attribute </xsl:text>
            <xsl:value-of select="eo:escape($attr)"/>
            <xsl:text> is not used in the </xsl:text>
            <xsl:choose>
              <xsl:when test="$formation/@name">
                <xsl:text>object </xsl:text>
                <xsl:value-of select="eo:escape($formation/@name)"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:text>anonymous object</xsl:text>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:element>
        </xsl:if>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
