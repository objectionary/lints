<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL-Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:math="http://www.w3.org/2005/xpath-functions/math" xmlns:eo="https://www.eolang.org" version="2.0" id="sprintf-constant-args">
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
  <xsl:function name="eo:count-formatters" as="xs:integer">
    <xsl:param name="text" as="xs:string"/>
    <xsl:variable name="txt" select="translate($text, '-', '')"/>
    <xsl:variable name="escaped" select="replace($txt, '(25)(25)', 'ESCAPED_PERCENT')"/>
    <xsl:variable name="strings" select="count(tokenize($escaped, '2573'))"/>
    <xsl:variable name="numbers" select="count(tokenize($escaped, '2564'))"/>
    <xsl:variable name="floats" select="count(tokenize($escaped, '2566'))"/>
    <xsl:variable name="bytes" select="count(tokenize($escaped, '2578'))"/>
    <xsl:variable name="bools" select="count(tokenize($escaped, '2562'))"/>
    <xsl:sequence select="$strings + $numbers + $floats + $bytes + $bools - 5"/>
  </xsl:function>
  <xsl:function name="eo:is-constant-string" as="xs:boolean">
    <xsl:param name="obj" as="node()"/>
    <xsl:sequence select="
      ($obj/@base = 'Φ.string' and $obj/o[1]/@base = 'Φ.bytes' and $obj/o[1]/o[1]/self::text())
      or
      ($obj/@base = 'Φ.bytes' and $obj/o[1]/self::text())
    "/>
  </xsl:function>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="//o[@base='Φ.txt.sprintf']">
        <xsl:variable name="text" select="o[1][@base='Φ.string']/o[1][@base='Φ.bytes']/o/text()"/>
        <xsl:variable name="formatters" select="eo:count-formatters($text)"/>
        <!-- Only check if format string has formatters -->
        <xsl:if test="$formatters > 0">
          <!-- Get the arguments (skip first child which is format string) -->
          <xsl:variable name="args" select="o[position() > 1]"/>
          <xsl:variable name="arg-count" select="count($args)"/>
          <!-- Check if all arguments are constant strings -->
          <xsl:variable name="all-constant">
            <xsl:for-each select="$args">
              <xsl:if test="not(eo:is-constant-string(.))">
                <xsl:sequence select="false()"/>
              </xsl:if>
            </xsl:for-each>
            <xsl:if test="$arg-count = 0">
              <xsl:sequence select="false()"/>
            </xsl:if>
          </xsl:variable>
          <!-- If format has formatters AND all args are constants, warn -->
          <xsl:if test="$arg-count > 0 and not($args/o[1][@base!='Φ.string' or @base!='Φ.bytes'])">
            <xsl:variable name="placeholder">
              <xsl:for-each select="tokenize($text, '-')">
                <xsl:value-of select="codepoints-to-string(eo:hex-to-placeholder(.))"/>
              </xsl:for-each>
            </xsl:variable>
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
              <xsl:text>The \"Φ.txt.sprintf\" object uses formatters (\"%s\", \"%d\", etc.) but all arguments are constant strings – consider using a literal string instead of \"</xsl:text>
              <xsl:value-of select="$placeholder"/>
              <xsl:text>\"</xsl:text>
            </defect>
          </xsl:if>
        </xsl:if>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
