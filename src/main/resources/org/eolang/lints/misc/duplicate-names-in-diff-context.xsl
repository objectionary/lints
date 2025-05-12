<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" version="2.0" id="duplicate-names-in-diff-context">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:import href="/org/eolang/funcs/special-name.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="//o[not(eo:special(@name))]">
        <xsl:variable name="namesakes" select="//o[@name=current()/@name]"/>
        <xsl:if test="count($namesakes)&gt;1">
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
            <xsl:text>Object "</xsl:text>
            <xsl:value-of select="@name"/>
            <xsl:text>" has same name as </xsl:text>
            <xsl:variable name="lines" select="$namesakes[@line and (not(current()/@line) or @line != current()/@line)]/@line"/>
            <xsl:variable name="empty" select="count($lines) != count($namesakes) - 1"/>
            <xsl:if test="count($lines) &gt; 0">
              <xsl:text>objects on lines </xsl:text>
              <xsl:value-of select="string-join($lines, ', ')"/>
              <xsl:if test="$empty">
                <xsl:text> and </xsl:text>
              </xsl:if>
            </xsl:if>
            <xsl:if test="$empty">
              <xsl:text>other objects in file</xsl:text>
            </xsl:if>
          </defect>
        </xsl:if>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
