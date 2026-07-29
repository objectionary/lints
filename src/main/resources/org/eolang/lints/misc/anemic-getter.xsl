<?xml version="1.0" encoding="UTF-8"?>
<!--
* SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
* SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" id="anemic-getter" version="2.0">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <!--
  A named object whose whole body is a reference to a sibling attribute of
  the same formation is an "anemic getter". For example, "title > t" inside
  a "[title] > book" formation just renames "title" without adding anything.
  Such renaming is redundant, since the original attribute may be used
  directly, so we complain about it here.
  -->
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="//o[@name and @name!='φ' and @base and matches(@base, '^ξ\.[^.]+$') and not(o)]">
        <xsl:variable name="ref" select="substring-after(@base, 'ξ.')"/>
        <xsl:if test="../o[@name=$ref]">
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
            <xsl:text>The object </xsl:text>
            <xsl:value-of select="eo:escape(@name)"/>
            <xsl:text> is a redundant getter, it just renames </xsl:text>
            <xsl:value-of select="eo:escape($ref)"/>
            <xsl:text> without adding anything, use </xsl:text>
            <xsl:value-of select="eo:escape($ref)"/>
            <xsl:text> directly instead</xsl:text>
          </defect>
        </xsl:if>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
