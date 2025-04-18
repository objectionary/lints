<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" id="unique-metas" version="2.0">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:output encoding="UTF-8"/>
  <xsl:variable name="unique" select="('version', 'architect', 'home', 'package')"/>
  <xsl:variable name="metas" select="/object/metas/meta"/>
  <xsl:variable name="heads" select="/object/metas/meta/head"/>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="$unique">
        <xsl:variable name="head" select="current()"/>
        <xsl:if test="count($heads[text() = $head])&gt;1">
          <xsl:element name="defect">
            <xsl:variable name="line" select="eo:lineno($metas[head = $head][2]/@line)"/>
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
            <xsl:text>There are more than one "+</xsl:text>
            <xsl:value-of select="$head"/>
            <xsl:text>" meta specified</xsl:text>
          </xsl:element>
        </xsl:if>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
