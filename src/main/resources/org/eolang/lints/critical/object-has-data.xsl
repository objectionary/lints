<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" version="2.0" id="object-has-data">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:import href="/org/eolang/parser/_funcs.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="//o[eo:has-data(.) and not(parent::o[@base='Q.org.eolang.bytes'])]">
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
          <xsl:text>Only object that have parent object with @base equal to "Q.org.eolang.bytes" may contain data, while</xsl:text>
          <xsl:choose>
            <xsl:when test="$parent/@base">
              <xsl:text> parent object with @base </xsl:text>
              <xsl:value-of select="eo:escape($parent/@base)"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text> anonymous parent object</xsl:text>
            </xsl:otherwise>
          </xsl:choose>
          <xsl:text> provided</xsl:text>
        </defect>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
