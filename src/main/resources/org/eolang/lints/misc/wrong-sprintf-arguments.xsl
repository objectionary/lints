<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:eo="https://www.eolang.org" version="2.0" id="wrong-sprintf-arguments">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <!-- Find arguments in tuple -->
  <xsl:template match="o" mode="arguments" as="xs:integer">
    <xsl:choose>
      <xsl:when test="@base='Q.org.eolang.tuple.empty'">
        <xsl:value-of select="0"/>
      </xsl:when>
      <xsl:when test="@base='Q.org.eolang.tuple'">
        <xsl:variable name="nested">
          <xsl:apply-templates select="o[1]" mode="arguments"/>
        </xsl:variable>
        <xsl:choose>
          <xsl:when test="count(o) &gt;= 2">
            <xsl:value-of select="$nested + 1"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="$nested"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="-1"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="//o[@base='Q.org.eolang.txt.sprintf']">
        <xsl:variable name="text" select="o[1][@base='Q.org.eolang.string']/o[1][@base='Q.org.eolang.bytes']/o/text()"/>
        <xsl:choose>
          <xsl:when test="count(o)&gt;2">
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
              <xsl:text>The "Q.org.eolang.txt.sprintf" object expects only 2 arguments, but </xsl:text>
              <xsl:value-of select="count(o)"/>
              <xsl:text> provided</xsl:text>
            </defect>
          </xsl:when>
          <xsl:when test="not($text)">
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
              <xsl:text>The first argument of "Q.org.eolang.txt.sprintf" object must be "Q.org.eolang.string" object, but </xsl:text>
              <xsl:value-of select="o[1]/@base"/>
              <xsl:text> provided</xsl:text>
            </defect>
          </xsl:when>
          <xsl:otherwise>
            <xsl:variable name="declared">
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
            <xsl:variable name="used">
              <xsl:apply-templates select="o[2]" mode="arguments"/>
            </xsl:variable>
            <xsl:choose>
              <xsl:when test="$used=-1">
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
                  <xsl:text>The second argument "Q.org.eolang.txt.sprintf" object must be a right structured "Q.org.eolang.tuple" object</xsl:text>
                </defect>
              </xsl:when>
              <xsl:otherwise>
                <xsl:if test="$declared!=$used">
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
