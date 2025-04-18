<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" version="2.0" id="no-attribute-formation">
  <xsl:import href="/org/eolang/parser/_funcs.xsl"/>
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/">
    <defects>
      <xsl:if test="not(/object[metas/meta[head='tests']])">
        <xsl:for-each select="//o[eo:abstract(.) and not(parent::o[@base='Q.org.eolang.bytes']) and not(o[@base='∅'])]">
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
            <xsl:attribute name="severity">warning</xsl:attribute>
            <xsl:attribute name="experimental">true</xsl:attribute>
            <xsl:text>The formation </xsl:text>
            <xsl:value-of select="eo:escape(@name)"/>
            <xsl:text> does not have any void attributes</xsl:text>
          </defect>
        </xsl:for-each>
      </xsl:if>
    </defects>
  </xsl:template>
</xsl:stylesheet>
