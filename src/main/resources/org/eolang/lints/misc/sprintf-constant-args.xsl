<?xml version="1.0" encoding="UTF-8"?>
<!--
* SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
* SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" version="2.0" id="sprintf-constant-args">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <!-- Collect the actual argument nodes of a "*"-built tuple, in order -->
  <xsl:template match="o" mode="constant-args" as="element()*">
    <xsl:if test="@base='Φ.tuple' and count(o) &gt;= 2">
      <xsl:apply-templates select="o[1]" mode="constant-args"/>
      <xsl:sequence select="o[2]"/>
    </xsl:if>
  </xsl:template>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="//o[@base='Φ.txt.sprintf'][count(o)=2][o[2][@base='Φ.tuple']]">
        <xsl:variable name="text" select="o[1][@base='Φ.string']/o[1][@base='Φ.bytes']/o/text()"/>
        <xsl:if test="$text">
          <xsl:variable name="formatters">
            <xsl:variable name="txt" select="translate($text, '-', '')"/>
            <!-- First, replace %% with a unique placeholder to avoid counting it as a formatter -->
            <xsl:variable name="escaped" select="replace($txt, '(25)(25)', 'ESCAPED_PERCENT')"/>
            <!-- %s -->
            <xsl:variable name="strings" select="count(tokenize($escaped, '2573'))"/>
            <!-- %d -->
            <xsl:variable name="numbers" select="count(tokenize($escaped, '2564'))"/>
            <!-- %f -->
            <xsl:variable name="floats" select="count(tokenize($escaped, '2566'))"/>
            <!-- %x -->
            <xsl:variable name="bytes" select="count(tokenize($escaped, '2578'))"/>
            <!-- %b -->
            <xsl:variable name="bools" select="count(tokenize($escaped, '2562'))"/>
            <xsl:value-of select="$strings + $numbers + $floats + $bytes + $bools - 5"/>
          </xsl:variable>
          <xsl:variable name="args" as="element()*">
            <xsl:apply-templates select="o[2]" mode="constant-args"/>
          </xsl:variable>
          <xsl:if test="$formatters &gt; 0 and count($args) &gt; 0 and count($args[not(@base='Φ.string')]) = 0 and count($args[not(o[1][@base='Φ.bytes']/o[1]/text())]) = 0">
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
                <xsl:text>warning</xsl:text>
              </xsl:attribute>
              <xsl:text>The "Φ.txt.sprintf" object is used with a constant format template and constant string arguments only; since the result is already known, a plain literal string should be used instead</xsl:text>
            </defect>
          </xsl:if>
        </xsl:if>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
