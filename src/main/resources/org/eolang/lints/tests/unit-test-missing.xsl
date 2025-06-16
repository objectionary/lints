<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" id="unit-test-without-phi" version="2.0">
  <xsl:import href="/org/eolang/parser/_funcs.xsl"/>
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/">
    <defects>
      <xsl:if test="count(//o[starts-with(@name, '+')]) = 0">
        <xsl:element name="defect">
          <xsl:attribute name="line">
            <xsl:value-of select="0"/>
          </xsl:attribute>
          <xsl:attribute name="severity">
            <xsl:text>warning</xsl:text>
          </xsl:attribute>
          <xsl:text>Tests not found for </xsl:text>
          <xsl:value-of select="eo:escape(/object/o/@name)"/>
        </xsl:element>
      </xsl:if>
    </defects>
  </xsl:template>
</xsl:stylesheet>
