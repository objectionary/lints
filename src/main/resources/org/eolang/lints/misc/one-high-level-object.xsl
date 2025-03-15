<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" version="2.0" id="one-high-level-object">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:variable name="program" select="/program/@name"/>
  <xsl:variable name="tested" select="/program/metas/meta[head='tests']"/>
  <xsl:variable name="objects" select="count(/program/objects/o)"/>
  <xsl:template match="/">
    <defects>
      <xsl:if test="$objects&gt;1 and not($tested)">
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
          <xsl:text>Program </xsl:text>
          <xsl:value-of select="eo:escape($program)"/>
          <xsl:text> has </xsl:text>
          <xsl:value-of select="$objects"/>
          <xsl:text> objects, while only 1 is allowed</xsl:text>
        </defect>
      </xsl:if>
    </defects>
  </xsl:template>
</xsl:stylesheet>
