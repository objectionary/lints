<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" id="atom-with-phi" version="2.0">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/">
    <defects>
      <xsl:apply-templates select="//o[@atom and o[@base='∅' and @name='@']]" mode="with-phi"/>
    </defects>
  </xsl:template>
  <xsl:template match="o" mode="with-phi">
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
      <xsl:text>Atoms must not have void '@' attribute</xsl:text>
    </defect>
  </xsl:template>
</xsl:stylesheet>
