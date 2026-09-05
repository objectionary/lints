<?xml version="1.0" encoding="UTF-8"?>
<!--
* SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
* SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:math="http://www.w3.org/2005/xpath-functions/math" xmlns:eo="https://www.eolang.org" version="2.0" id="sprintf-without-formatters">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:function name="eo:hex-to-placeholder" as="xs:integer">
    <xsl:param name="hex" as="xs:string"/>
    <xsl:variable name="hex-upper" select="upper-case($hex)"/>
    <xsl:variable name="length" select="string-length($hex-upper)"/>
    <xsl:variable name="decimal" select="sum(for $i in 1 to $length return (index-of(string-to-codepoints('0123456789ABCDEF'), string-to-codepoints(substring($hex-upper, $i, 1))) - 1) * xs:integer(math:pow(16, $length - $i)))"/>
    <xsl:sequence select="$decimal"/>
  </xsl:function>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="//o[@base='.printf'][o[not(@as)][@base='Φ.string']/o[@base='Φ.bytes']/o/text()]">
        <xsl:variable name="text" select="o[not(@as)][@base='Φ.string']/o[@base='Φ.bytes']/o/text()"/>
        <xsl:variable name="placeholder">
          <xsl:for-each select="tokenize($text, '-')">
            <xsl:value-of select="codepoints-to-string(eo:hex-to-placeholder(.))"/>
          </xsl:for-each>
        </xsl:variable>
        <!-- A specifier reads as %[N$][flags][width][.precision]conversion, and a
        doubled percent is a literal one rather than the start of a specifier -->
        <xsl:if test="not(matches(replace($placeholder, '%%', ''), '%(\d+\$)?[-0]*\d*(\.\d+)?[sdfxb]'))">
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
            <xsl:text>According to the formatting template of the ".printf" object, </xsl:text>
            <xsl:value-of select="eo:escape($placeholder)"/>
            <xsl:text> does not have any supported formatters ("%s", "%d", "%f", "%x", "%b"), which makes no sense to use ".printf"</xsl:text>
          </defect>
        </xsl:if>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
