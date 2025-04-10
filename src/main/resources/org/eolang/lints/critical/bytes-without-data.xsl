<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" id="bytes-without-data" version="2.0">
  <xsl:import href="/org/eolang/parser/_funcs.xsl"/>
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/">
    <defects>
      <xsl:apply-templates select="//o[not(eo:has-data(.)) and parent::o[@base='Q.org.eolang.bytes']]" mode="with-data"/>
    </defects>
  </xsl:template>
  <xsl:template match="o" mode="with-data">
    <defect>
      <xsl:variable name="parent" select="parent::o"/>
      <xsl:variable name="line" select="eo:lineno($parent/@line)"/>
      <xsl:attribute name="line">
        <xsl:value-of select="$line"/>
      </xsl:attribute>
      <xsl:if test="$line = '0'">
        <xsl:attribute name="context">
          <xsl:value-of select="eo:defect-context($parent)"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:attribute name="severity">critical</xsl:attribute>
      <xsl:text>Objects with parent @base equal to "Q.org.eolang.bytes" must contain data, while </xsl:text>
      <xsl:value-of select="eo:escape($parent/@name)"/>
      <xsl:text> object does not</xsl:text>
    </defect>
  </xsl:template>
</xsl:stylesheet>
