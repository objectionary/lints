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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:eo="https://www.eolang.org" version="2.0" id="wrong-sprintf-arguments">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <!-- Find arguments in tuple -->
  <xsl:template match="o" mode="arguments" as="xs:integer">
    <xsl:choose>
      <xsl:when test="@base='.with' and count(o)=2">
        <xsl:variable name="nested">
          <xsl:apply-templates select="o[1]" mode="arguments"/>
        </xsl:variable>
        <xsl:choose>
          <xsl:when test="$nested!=-1">
            <xsl:value-of select="$nested + 1"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="-1"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:when test="@base='Q.org.eolang.tuple.empty'">
        <xsl:value-of select="0"/>
      </xsl:when>
      <xsl:when test="@base='Q.org.eolang.tuple.empty.with' and count(o)=1">
        <xsl:value-of select="1"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="-1"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="//o[@base='Q.org.eolang.txt.sprintf']">
        <xsl:variable name="text" select="o[1][@base='Q.org.eolang.string']/o[1][@base='Q.org.eolang.bytes']/text()"/>
        <xsl:choose>
          <xsl:when test="count(o)&gt;2">
            <defect>
              <xsl:attribute name="line">
                <xsl:value-of select="eo:lineno(@line)"/>
              </xsl:attribute>
              <xsl:attribute name="severity">
                <xsl:text>warning</xsl:text>
              </xsl:attribute>
              <xsl:text>The "Q.org.eolang.txt.sprintf" object expects only 2 arguments, but </xsl:text>
              <xsl:value-of select="count(o)"/>
              <xsl:text> provided</xsl:text>
            </defect>
          </xsl:when>
          <xsl:when test="not($text)">
            <defect>
              <xsl:attribute name="line">
                <xsl:value-of select="eo:lineno(@line)"/>
              </xsl:attribute>
              <xsl:attribute name="severity">
                <xsl:text>warning</xsl:text>
              </xsl:attribute>
              <xsl:text>The first argument of "Q.org.eolang.txt.sprintf" object must be "Q.org.eolang.string" object, but </xsl:text>
              <xsl:value-of select="o[1]/@base"/>
              <xsl:text> provided</xsl:text>
            </defect>
          </xsl:when>
          <xsl:otherwise>
            <xsl:variable name="declared">
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
            <xsl:variable name="used">
              <xsl:apply-templates select="o[2]" mode="arguments"/>
            </xsl:variable>
            <xsl:choose>
              <xsl:when test="$used=-1">
                <defect>
                  <xsl:attribute name="line">
                    <xsl:value-of select="eo:lineno(@line)"/>
                  </xsl:attribute>
                  <xsl:attribute name="severity">
                    <xsl:text>warning</xsl:text>
                  </xsl:attribute>
                  <xsl:text>The second argument "Q.org.eolang.txt.sprintf" object must be a right structured "Q.org.eolang.tuple" object built via ".with" method</xsl:text>
                </defect>
              </xsl:when>
              <xsl:otherwise>
                <xsl:if test="$declared!=$used">
                  <defect>
                    <xsl:attribute name="line">
                      <xsl:value-of select="eo:lineno(@line)"/>
                    </xsl:attribute>
                    <xsl:attribute name="severity">
                      <xsl:text>warning</xsl:text>
                    </xsl:attribute>
                    <xsl:text>According to the formatting template of the "Q.org.eolang.txt.sprintf" object, a tuple of </xsl:text>
                    <xsl:value-of select="$declared"/>
                    <xsl:text> elements is expected as the second argument of it, while a tuple of </xsl:text>
                    <xsl:value-of select="$used"/>
                    <xsl:text> element(s) is provided</xsl:text>
                  </defect>
                </xsl:if>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
