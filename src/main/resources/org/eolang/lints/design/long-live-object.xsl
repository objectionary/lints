<?xml version="1.0" encoding="UTF-8"?>
<!--
* SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
* SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:eo="https://www.eolang.org" version="2.0" id="long-live-object">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="//o[@name and not(@name='φ')]">
        <xsl:variable name="def" select="."/>
        <xsl:variable name="usage" select="(//o[@base=concat('ξ.', $def/@name) and number(@line) &gt; number($def/@line)])[1]"/>
        <xsl:if test="$usage and not($def/ancestor::o[@name = $def/@name])">
          <xsl:variable name="between" select="count($def/../o[number(@line) &gt; number($def/@line) and number(@line) &lt; number($usage/@line)])"/>
          <xsl:if test="$between &gt; 5">
            <defect>
              <xsl:variable name="line" select="eo:lineno($def/@line)"/>
              <xsl:attribute name="line">
                <xsl:value-of select="$line"/>
              </xsl:attribute>
              <xsl:if test="$line = '0'">
                <xsl:attribute name="context">
                  <xsl:value-of select="eo:defect-context($def)"/>
                </xsl:attribute>
              </xsl:if>
              <xsl:attribute name="severity">warning</xsl:attribute>
              <xsl:text>The object </xsl:text>
              <xsl:value-of select="eo:escape($def/@name)"/>
              <xsl:text> is defined </xsl:text>
              <xsl:value-of select="$between"/>
              <xsl:text> attributes away from its first usage, which is too far. Move it closer to the usage</xsl:text>
            </defect>
          </xsl:if>
        </xsl:if>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
