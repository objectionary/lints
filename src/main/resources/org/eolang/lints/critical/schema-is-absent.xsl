<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" id="schema-is-absent" version="2.0">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/">
    <defects>
      <xsl:apply-templates select="/object[not(@xsi:noNamespaceSchemaLocation)]"/>
    </defects>
  </xsl:template>
  <xsl:template match="/object[not(@xsi:noNamespaceSchemaLocation)]">
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
        <xsl:text>critical</xsl:text>
      </xsl:attribute>
      <xsl:text>There is no XSD schema attached to the XML document, </xsl:text>
      <xsl:text>while "xsi:noNamespaceSchemaLocation" is expected</xsl:text>
    </xsl:element>
  </xsl:template>
</xsl:stylesheet>
