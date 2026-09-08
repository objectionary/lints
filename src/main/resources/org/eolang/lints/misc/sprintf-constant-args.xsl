<?xml version="1.0" encoding="UTF-8"?>
<!--
* SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
* SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:math="http://www.w3.org/2005/xpath-functions/math" xmlns:eo="https://www.eolang.org" version="2.0" id="sprintf-constant-args">
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
  <xsl:function name="eo:hex-to-placeholder" as="xs:integer">
    <xsl:param name="hex" as="xs:string"/>
    <xsl:variable name="hex-upper" select="upper-case($hex)"/>
    <xsl:variable name="length" select="string-length($hex-upper)"/>
    <xsl:variable name="decimal" select="sum(for $i in 1 to $length return (index-of(string-to-codepoints('0123456789ABCDEF'), string-to-codepoints(substring($hex-upper, $i, 1))) - 1) * xs:integer(math:pow(16, $length - $i)))"/>
    <xsl:sequence select="$decimal"/>
  </xsl:function>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="//o[@base='.printf' or @base='Φ.txt.sprintf'][count(o)=2][o[2][@base='Φ.tuple']]">
        <xsl:variable name="text" select="o[1][@base='Φ.string']/o[1][@base='Φ.bytes']/o/text()"/>
        <xsl:if test="$text">
          <xsl:variable name="placeholder">
            <xsl:for-each select="tokenize($text, '-')">
              <xsl:value-of select="codepoints-to-string(eo:hex-to-placeholder(.))"/>
            </xsl:for-each>
          </xsl:variable>
          <!--
          A specifier reads as %[N$][flags][width][.precision]conversion, and a
          doubled percent is a literal one rather than the start of a specifier
          -->
          <xsl:variable name="formatters" select="count(tokenize(replace($placeholder, '%%', ''), '%(\d+\$)?[-0]*\d*(\.\d+)?[sdfxb]')) - 1"/>
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
              <xsl:text>The ".printf" object is used with a constant format template and constant string arguments only; since the result is already known, a plain literal string should be used instead</xsl:text>
            </defect>
          </xsl:if>
        </xsl:if>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
