<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" id="atom-without-rt" version="2.0">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/">
    <defects>
      <xsl:if test="not(/program/metas/meta[head='rt'])">
        <xsl:for-each select="//o[@name='λ']">
          <xsl:element name="defect">
            <xsl:attribute name="line">
              <xsl:value-of select="eo:lineno(@line)"/>
            </xsl:attribute>
            <xsl:attribute name="severity">
              <xsl:text>error</xsl:text>
            </xsl:attribute>
            <xsl:text>The </xsl:text>
            <xsl:value-of select="eo:escape(@name)"/>
            <xsl:text> atom is defined without the +rt meta, which doesn't make any sense</xsl:text>
          </xsl:element>
        </xsl:for-each>
      </xsl:if>
    </defects>
  </xsl:template>
</xsl:stylesheet>
