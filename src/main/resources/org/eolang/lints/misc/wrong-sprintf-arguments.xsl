<?xml version="1.0" encoding="UTF-8"?>
<!--
* SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
* SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:eo="https://www.eolang.org" version="2.0" id="wrong-sprintf-arguments">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <!-- One format specifier, matched over the hex bytes of the template:
  a percent, an optional N$ position, the "-" and "0" flags, a width,
  an optional .precision, and one of the five conversion letters -->
  <xsl:variable name="eo:specifier" select="'25((3[0-9])+24)?(2D|30)*(3[0-9])*(2E(3[0-9])+)?(73|64|66|78|62)'"/>
  <!-- Find arguments in tuple -->
  <xsl:template match="o" mode="arguments" as="xs:integer">
    <xsl:choose>
      <xsl:when test="@base='Φ.tuple.empty'">
        <xsl:value-of select="0"/>
      </xsl:when>
      <xsl:when test="@base='Φ.tuple'">
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
      <xsl:for-each select="//o[@base='.printf']">
        <xsl:variable name="text" select="o[not(@as)][1][@base='Φ.string']/o[1][@base='Φ.bytes']/o/text()"/>
        <xsl:choose>
          <xsl:when test="count(o[@as])&gt;1">
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
              <xsl:text>The ".printf" object expects only 1 argument, but </xsl:text>
              <xsl:value-of select="count(o[@as])"/>
              <xsl:text> provided</xsl:text>
            </defect>
          </xsl:when>
          <xsl:otherwise>
            <!-- The bytes of the template, with a doubled percent taken out,
            since it stands for a literal one and starts no specifier -->
            <xsl:variable name="escaped" select="replace(translate($text, '-', ''), '2525', '')"/>
            <!-- A specifier is %[N$][flags][width][.precision]conversion, in bytes -->
            <xsl:variable name="declared" select="count(tokenize($escaped, $eo:specifier)) - 1"/>
            <xsl:variable name="used">
              <xsl:apply-templates select="o[@as][1]" mode="arguments"/>
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
                  <xsl:text>The argument of the ".printf" object must be a right structured "Φ.tuple" object</xsl:text>
                </defect>
              </xsl:when>
              <xsl:otherwise>
                <xsl:if test="$declared!=$used and o[not(@as)][1]/@base = 'Φ.string' and not(matches($escaped, '25(3[0-9])+24'))">
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
                    <xsl:text>According to the formatting template of the ".printf" object, a tuple of </xsl:text>
                    <xsl:value-of select="$declared"/>
                    <xsl:text> element(s) is expected as its argument, while a tuple of </xsl:text>
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
