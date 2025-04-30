<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" id="incorrect-package" version="2.0">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="/object/metas/meta">
        <xsl:variable name="meta-head" select="head"/>
        <xsl:variable name="meta-tail" select="tail"/>
        <xsl:variable name="first" select="normalize-space(substring-before(concat($meta-tail, ' '), ' '))"/>
        <xsl:if test="$meta-head='package' and not(matches($first, '^[a-zA-Z0-9_]+(\.[a-zA-Z0-9_]+|\$[a-zA-Z0-9_]*)*$|[\p{L}\p{M}\p{N}_]+(\.[\p{L}\p{M}\p{N}_]+)*$'))">
          <xsl:element name="defect">
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
            <xsl:text>The format of the +package meta is wrong: </xsl:text>
            <xsl:value-of select="eo:escape($first)"/>
          </xsl:element>
        </xsl:if>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
