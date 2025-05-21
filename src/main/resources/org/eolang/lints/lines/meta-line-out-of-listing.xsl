<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" id="meta-line-out-of-listing" version="2.0">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:variable name="max" select="count(tokenize(/object/listing, '&#10;'))"/>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="/object/metas/meta[number(@line) and @line &gt; $max]">
        <xsl:element name="defect">
          <xsl:attribute name="line">
            <xsl:value-of select="eo:lineno(@line)"/>
          </xsl:attribute>
          <xsl:attribute name="severity">
            <xsl:text>error</xsl:text>
          </xsl:attribute>
          <xsl:text>The line </xsl:text>
          <xsl:value-of select="eo:lineno(@line)"/>
          <xsl:text> is out of listing, which contains only </xsl:text>
          <xsl:value-of select="$max"/>
          <xsl:text> lines</xsl:text>
        </xsl:element>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
