<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" id="prohibited-package" version="2.0">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:variable name="white-list">
    <a>as-phi</a>
    <a>bytes</a>
    <a>cti</a>
    <a>dataized</a>
    <a>error</a>
    <a>false</a>
    <a>go</a>
    <a>i16</a>
    <a>i32</a>
    <a>i64</a>
    <a>malloc</a>
    <a>nan</a>
    <a>negative-infinity</a>
    <a>number</a>
    <a>positive-infinity</a>
    <a>rust</a>
    <a>seq</a>
    <a>string</a>
    <a>switch</a>
    <a>true</a>
    <a>try</a>
    <a>tuple</a>
    <a>while</a>
  </xsl:variable>
  <xsl:variable name="name" select="/object/o[1]/@name"/>
  <xsl:template match="/">
    <defects>
      <xsl:variable name="tested" select="/object/metas/meta[head='tests']"/>
      <xsl:for-each select="/object/metas/meta">
        <xsl:variable name="meta-head" select="head"/>
        <xsl:variable name="meta-tail" select="tail"/>
        <xsl:if test="not($tested) and $meta-head='package' and $meta-tail='org.eolang' and not($white-list/a=$name)">
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
            <xsl:text>The "org.eolang" package is reserved for internal object only</xsl:text>
          </xsl:element>
        </xsl:if>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
