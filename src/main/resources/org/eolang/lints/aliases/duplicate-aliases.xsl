<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" id="duplicate-aliases" version="2.0">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="/program/metas/meta[head='alias']">
        <xsl:variable name="name" select="tokenize(tail, ' ')[1]"/>
        <xsl:if test="preceding-sibling::meta[head='alias' and tokenize(tail, ' ')[1]=$name]">
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
              <xsl:text>error</xsl:text>
            </xsl:attribute>
            <xsl:text>The alias </xsl:text>
            <xsl:value-of select="eo:escape($name)"/>
            <xsl:text> is duplicated</xsl:text>
          </xsl:element>
        </xsl:if>
      </xsl:for-each>
    </defects>
  </xsl:template>
  <xsl:template match="meta" mode="dups">
    <xsl:for-each select="/program/metas/meta[head='alias']">
      <xsl:variable name="x" select="."/>
      <xsl:if test="preceding-sibling::o/@name = $x/@name">
        <error>
          <xsl:attribute name="line">
            <xsl:value-of select="if (@line) then @line else '0'"/>
          </xsl:attribute>
          <xsl:attribute name="severity">
            <xsl:text>error</xsl:text>
          </xsl:attribute>
          <xsl:text>The name </xsl:text>
          <xsl:value-of select="eo:escape(@name)"/>
          <xsl:text> is already in use</xsl:text>
        </error>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>
</xsl:stylesheet>
