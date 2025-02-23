<?xml version="1.0"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" id="atom-fqns" version="2.0">
  <xsl:template match="node()|@*">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  <xsl:template match="o">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:if test="@atom">
        <xsl:attribute name="fqn">
          <xsl:call-template name="generate-fqn"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:apply-templates select="node()"/>
    </xsl:copy>
  </xsl:template>
  <xsl:template name="generate-fqn">
    <xsl:for-each select="ancestor-or-self::o">
      <xsl:if test="position() != 1">.</xsl:if>
      <xsl:value-of select="@name"/>
    </xsl:for-each>
  </xsl:template>
</xsl:stylesheet>
