<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" id="rt-without-atoms" version="2.0">
  <xsl:import href="/org/eolang/parser/_funcs.xsl"/>
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/">
    <defects>
      <xsl:if test="not(//o[eo:atom(.)])">
        <xsl:if test="/program/metas/meta[head='rt']">
          <xsl:element name="defect">
            <xsl:attribute name="line">
              <xsl:value-of select="eo:lineno(/program/metas/meta[head='rt'][1]/@line)"/>
            </xsl:attribute>
            <xsl:attribute name="severity">
              <xsl:text>error</xsl:text>
            </xsl:attribute>
            <xsl:text>Using the +rt meta without any atoms in the program doesn't make sense</xsl:text>
          </xsl:element>
        </xsl:if>
      </xsl:if>
    </defects>
  </xsl:template>
</xsl:stylesheet>
