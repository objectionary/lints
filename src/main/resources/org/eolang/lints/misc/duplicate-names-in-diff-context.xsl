<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" version="2.0" id="duplicate-names-in-diff-context">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="//o[@name!='@']">
        <xsl:if test="count(//o[@name=current()/@name])&gt;1">
          <defect>
            <xsl:attribute name="line">
              <xsl:value-of select="eo:lineno(@line)"/>
            </xsl:attribute>
            <xsl:attribute name="severity">warning</xsl:attribute>
            <xsl:text>Object </xsl:text>
            <xsl:value-of select="@name"/>
            <xsl:text> has same name as object on line </xsl:text>
            <xsl:for-each select="//o[@line != current()/@line and @name = current()/@name]">
              <xsl:value-of select="@line"/>
              <xsl:text>, </xsl:text>
            </xsl:for-each>
            <xsl:text>that is not good</xsl:text>
          </defect>
        </xsl:if>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
