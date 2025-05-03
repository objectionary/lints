<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
  ~ SPDX-License-Identifier: MIT
  -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:eo="https://www.eolang.org" id="base-must-be-merged" version="2.0">
  <xsl:import href="/org/eolang/parser/_funcs.xsl"/>
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="//o[not(eo:abstract(.)) and parent::o[starts-with(@base, '.')] and not(starts-with(@base, '.')) and not(text())]">
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
          <xsl:text>Object </xsl:text>
          <xsl:if test="@name">
            <xsl:value-of select="@name"/>
            <xsl:text> </xsl:text>
          </xsl:if>
          <xsl:text>can be merged with its parent</xsl:text>
        </defect>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
