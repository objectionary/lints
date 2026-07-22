<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" id="unused-alias" version="2.0">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <!-- Индексируем все элементы o по их атрибуту @base -->
  <xsl:key name="base-by-name" match="o[@base]" use="@base"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="/object/metas/meta[head='alias' and count(part)=2]">
        <xsl:variable name="full-name" select="tokenize(tail, ' ')[1]"/>
        <xsl:variable name="alias-name" select="tokenize(tail, ' ')[last()]"/>
        <!-- Проверяем, используется ли полное имя -->
        <xsl:if test="count(key('base-by-name', $full-name))=0">
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
              <xsl:text>error</xsl:text>
            </xsl:attribute>
            <xsl:text>The alias </xsl:text>
            <xsl:value-of select="eo:escape($alias-name)"/>
            <xsl:text> is not used, but defined as </xsl:text>
            <xsl:value-of select="eo:escape(tail)"/>
          </xsl:element>
        </xsl:if>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>