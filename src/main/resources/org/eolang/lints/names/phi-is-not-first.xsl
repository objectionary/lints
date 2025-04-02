<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" version="2.0" id="phi-is-not-first">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/">
    <defects>
      <xsl:apply-templates select="//o[o[@name='@'] and o[@name='@']/preceding-sibling::o[not(@base='∅')]]" mode="unordered"/>
    </defects>
  </xsl:template>
  <xsl:template match="o" mode="unordered">
    <defect>
      <xsl:variable name="line" select="eo:lineno(o[@name='@']/@line)"/>
      <xsl:attribute name="line">
        <xsl:value-of select="$line"/>
      </xsl:attribute>
      <xsl:if test="$line = '0'">
        <xsl:attribute name="context">
          <xsl:value-of select="eo:defect-context(.)"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:attribute name="severity">warning</xsl:attribute>
      <xsl:text>The "@" object must go first (except void attributes ordering)</xsl:text>
    </defect>
  </xsl:template>
</xsl:stylesheet>
