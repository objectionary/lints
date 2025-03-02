<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" id="unsorted-metas" version="2.0">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="/program/metas/meta[not(head='spdx')]">
        <xsl:variable name="meta-text" select="concat(head, ' ', tail)"/>
        <xsl:variable name="previous" select="(preceding-sibling::meta[not(head = 'spdx')])[1]"/>
        <xsl:variable name="sibling-text" select="concat($previous/head/text(), ' ', $previous/tail/text())"/>
        <xsl:if test="$meta-text &lt; $sibling-text">
          <xsl:element name="defect">
            <xsl:attribute name="line">
              <xsl:value-of select="eo:lineno(@line)"/>
            </xsl:attribute>
            <xsl:attribute name="severity">
              <xsl:text>warning</xsl:text>
            </xsl:attribute>
            <xsl:text>The </xsl:text>
            <xsl:value-of select="eo:escape($meta-text)"/>
            <xsl:text> meta is out of order</xsl:text>
          </xsl:element>
        </xsl:if>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
