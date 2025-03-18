<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:eo="https://www.eolang.org" version="2.0" id="object-does-not-match-filename">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:variable name="package" select="/program/metas/meta[head='package'][1]"/>
  <xsl:template match="/">
    <defects>
      <xsl:if test="not(/program/metas/meta[head='tests']) and /program/@source">
        <xsl:apply-templates select="/program/objects/o" mode="live"/>
      </xsl:if>
    </defects>
  </xsl:template>
  <xsl:template match="o" mode="live">
    <xsl:variable name="opath" as="xs:string">
      <xsl:choose>
        <xsl:when test="$package">
          <xsl:value-of select="concat($package/tail/text(), '.', @name)"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="@name"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="source" as="xs:string" select="replace($opath, '\.', '/')"/>
    <xsl:if test="not(starts-with(/program/@source, concat($source, '.')))">
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
        <xsl:text>It is expected that the </xsl:text>
        <xsl:value-of select="eo:escape($opath)"/>
        <xsl:text> object is coming from either </xsl:text>
        <xsl:value-of select="eo:escape(concat($source, '.eo'))"/>
        <xsl:text> or </xsl:text>
        <xsl:value-of select="eo:escape(concat($source, '.phi'))"/>
        <xsl:text> file, however its "program/@source" points to </xsl:text>
        <xsl:value-of select="eo:escape(/program/@source)"/>
      </defect>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>
