<?xml version="1.0" encoding="UTF-8"?>
<!--
* SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
* SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" id="invalid-name-notation" version="2.0">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:import href="/org/eolang/funcs/special-name.xsl"/>
  <xsl:import href="/org/eolang/funcs/test-name.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="//o[@name and not(eo:special(@name)) and not(eo:test-name(@name)) and not(matches(@name, '^[a-z]+(-[a-z]+)*$'))]">
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
          <xsl:text>Object name </xsl:text>
          <xsl:value-of select="eo:escape(@name)"/>
          <xsl:text> must match the regular expression [a-z]+(-[a-z]+)*</xsl:text>
        </defect>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
