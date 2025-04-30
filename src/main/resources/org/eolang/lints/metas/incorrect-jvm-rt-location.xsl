<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" id="incorrect-jvm-rt-location" version="2.0">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="/object/metas/meta[head/text()='rt' and count(part) &gt; 1]">
        <xsl:variable name="meta-tail" select="tail"/>
        <xsl:variable name="runtime" select="normalize-space(substring-before(concat($meta-tail, ' '), ' '))"/>
        <xsl:variable name="location" select="normalize-space(substring-after($meta-tail, ' '))"/>
        <xsl:if test="$runtime='jvm' and not(matches($location, '^([a-zA-Z0-9_.-]+):([a-zA-Z0-9_.-]+):(\d+\.\d+\.\d+)$'))">
          <xsl:element name="defect">
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
            <xsl:text>The format of the location of the "jvm" runtime is wrong: </xsl:text>
            <xsl:value-of select="eo:escape($location)"/>
          </xsl:element>
        </xsl:if>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
