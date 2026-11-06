<?xml version="1.0" encoding="UTF-8"?>
<!--
* SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
* SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" xmlns:xs="http://www.w3.org/2001/XMLSchema" id="unsorted-metas" version="2.0">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:function name="eo:written" as="xs:string">
    <xsl:param name="meta"/>
    <xsl:sequence select="if ($meta/head = 'alias' and $meta/part[2]) then concat($meta/head, ' ', replace($meta/part[last()], '^Φ\.', '')) else concat($meta/head, ' ', $meta/tail)"/>
  </xsl:function>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="/object/metas/meta">
        <xsl:variable name="meta-text" select="eo:written(.)"/>
        <xsl:variable name="previous" select="preceding-sibling::meta[1]"/>
        <xsl:variable name="previous-text" select="eo:written($previous)"/>
        <xsl:if test="$meta-text &lt; $previous-text">
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
            <xsl:text>The </xsl:text>
            <xsl:value-of select="eo:escape($meta-text)"/>
            <xsl:text> meta is out of order</xsl:text>
          </xsl:element>
        </xsl:if>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
