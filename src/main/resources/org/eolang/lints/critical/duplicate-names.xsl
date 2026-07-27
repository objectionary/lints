<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" id="duplicate-names" version="2.0">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <!-- Добавляем ключ для группировки по родителю и имени -->
  <xsl:key name="named-o-by-parent" match="o[@name]" use="concat(generate-id(..), '|', @name)"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="//o|/object">
        <xsl:apply-templates select="." mode="dups"/>
      </xsl:for-each>
    </defects>
  </xsl:template>
  <xsl:template match="o|object" mode="dups">
    <xsl:for-each select="o[@name]">
      <xsl:variable name="x" select="."/>
      <!-- Получаем группу по родителю и имени -->
      <xsl:variable name="group" select="key('named-o-by-parent', concat(generate-id($x/..), '|', $x/@name))"/>
      <!-- Если это не первый элемент в группе – это дубликат -->
      <xsl:if test="count($group) > 1 and generate-id($x) != generate-id($group[1])">
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
          <xsl:text>The name </xsl:text>
          <xsl:value-of select="eo:escape(@name)"/>
          <xsl:text> is already in use</xsl:text>
        </xsl:element>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>
</xsl:stylesheet>
