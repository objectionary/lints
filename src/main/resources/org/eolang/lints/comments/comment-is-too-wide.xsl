<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" id="comment-is-too-wide" version="2.0">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/">
    <xsl:variable name="max" select="100"/>
    <defects>
      <xsl:for-each select="/object/comments/comment">
        <xsl:variable name="line" select="if (@line) then @line else '0'"/>
        <xsl:variable name="lines" select="tokenize(replace(., '\\n', '&#10;'), '&#10;')"/>
        <xsl:choose>
          <xsl:when test="count($lines) &gt; 1">
            <xsl:for-each select="$lines[string-length(.) &gt; $max]">
              <xsl:element name="defect">
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
                <xsl:text>The comment line width is </xsl:text>
                <xsl:value-of select="string-length(.)"/>
                <xsl:text>, while </xsl:text>
                <xsl:value-of select="$max"/>
                <xsl:text> is max allowed</xsl:text>
              </xsl:element>
            </xsl:for-each>
          </xsl:when>
          <xsl:otherwise>
            <xsl:if test="string-length(.) &gt; $max">
              <xsl:element name="defect">
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
                <xsl:text>The comment width is </xsl:text>
                <xsl:value-of select="string-length(.)"/>
                <xsl:text>, while </xsl:text>
                <xsl:value-of select="$max"/>
                <xsl:text> is max allowed</xsl:text>
              </xsl:element>
            </xsl:if>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
