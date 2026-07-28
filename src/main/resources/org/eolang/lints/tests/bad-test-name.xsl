<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" id="bad-test-name" version="2.0">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="//o[@name]">
        <xsl:call-template name="check-name"/>
      </xsl:for-each>
    </defects>
  </xsl:template>
  <xsl:template name="check-name">
    <xsl:variable name="name" select="@name"/>
    <xsl:variable name="prefixes" select="('can-', 'cannot-', 'accepts-', 'rejects-', 'stops-on-')"/>
    <xsl:variable name="is-valid">
      <xsl:for-each select="$prefixes">
        <xsl:if test="starts-with($name, .)">
          <xsl:text>true</xsl:text>
        </xsl:if>
      </xsl:for-each>
    </xsl:variable>
    <xsl:if test="string-length($is-valid) = 0">
      <xsl:element name="defect">
        <xsl:attribute name="line">
          <xsl:value-of select="eo:lineno(@line)"/>
        </xsl:attribute>
        <xsl:attribute name="severity">
          <xsl:text>warning</xsl:text>
        </xsl:attribute>
        <xsl:text>Test object name "</xsl:text>
        <xsl:value-of select="eo:escape($name)"/>
        <xsl:text>" does not start with one of: can-, cannot-, accepts-, rejects-, stops-on-</xsl:text>
      </xsl:element>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>
