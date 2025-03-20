<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" id="atom-with-data" version="2.0">
  <xsl:import href="/org/eolang/parser/_funcs.xsl"/>
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/">
    <defects>
      <xsl:apply-templates select="//o[eo:atom(.)]" mode="atom"/>
    </defects>
  </xsl:template>
  <xsl:template match="o" mode="atom">
    <xsl:variable name="data" select="normalize-space(string-join(text(), ''))"/>
    <xsl:if test="$data != ''">
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
        <xsl:attribute name="severity">critical</xsl:attribute>
        <xsl:text>Atoms must not contain data, while this object contains </xsl:text>
        <xsl:value-of select="eo:escape($data)"/>
      </defect>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>
