<?xml version="1.0" encoding="UTF-8"?>
<!--
The MIT License (MIT)

Copyright (c) 2016-2025 Objectionary.com

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included
in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:math="http://www.w3.org/2005/xpath-functions/math" xmlns:eo="https://www.eolang.org" version="2.0" id="sprintf-without-formatters">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
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
      <xsl:for-each select="//o[@base='Q.org.eolang.txt.sprintf']">
        <xsl:variable name="text" select="o[1][@base='Q.org.eolang.string']/o[1][@base='Q.org.eolang.bytes']/text()"/>
        <xsl:variable name="txt" select="translate($text, '-', '')"/>
        <xsl:variable name="formatters">
          <xsl:variable name="txt" select="translate($text, '-', '')"/>
          <!-- %s -->
          <xsl:variable name="strings" select="count(tokenize($txt, '2573'))"/>
          <!-- %d -->
          <xsl:variable name="numbers" select="count(tokenize($txt, '2564'))"/>
          <!-- %f -->
          <xsl:variable name="floats" select="count(tokenize($txt, '2566'))"/>
          <!-- %x -->
          <xsl:variable name="bytes" select="count(tokenize($txt, '2578'))"/>
          <!-- %b -->
          <xsl:variable name="bools" select="count(tokenize($txt, '2562'))"/>
          <xsl:value-of select="$strings + $numbers + $floats + $bytes + $bools - 5"/>
        </xsl:variable>
        <xsl:if test="$formatters = 0">
          <xsl:variable name="placeholder">
            <xsl:for-each select="tokenize($text, '-')">
              <xsl:value-of select="codepoints-to-string(eo:hex-to-placeholder(.))"/>
            </xsl:for-each>
          </xsl:variable>
          <defect>
            <xsl:attribute name="line">
              <xsl:value-of select="eo:lineno(@line)"/>
            </xsl:attribute>
            <xsl:attribute name="severity">
              <xsl:text>warning</xsl:text>
            </xsl:attribute>
            <xsl:text>According to the formatting template of the "Q.org.eolang.txt.sprintf" object, </xsl:text>
            <xsl:value-of select="eo:escape($placeholder)"/>
            <xsl:text> does not have any formatters, which makes no sense to use "Q.org.eolang.txt.sprintf"</xsl:text>
          </defect>
        </xsl:if>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
