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
  A named object whose whole body is a reference to something the code can
  already reach, unchanged, right where the object sits is an "anemic getter".
  Two shapes of body qualify: a sibling attribute of the same formation, as in
  "title > t" inside a "[title] > book" formation, and a bare chain of "ξ" and
  "ρ" hops, as in "^ > f" or "$ > f", which "^" and "$" already say. Such
  renaming is redundant, since the original may be used directly, so we
  complain about it here. A reference that reaches past the hops for an
  attribute, such as "^.bar", names something other than the hop itself, so it
  is not a rename and stays out of scope.
  -->
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="//o[@name and @name!='φ' and not(@base='ξ' and @name='xi🌵') and @base and starts-with(@base, 'ξ') and not(o)]">
        <xsl:variable name="ref" select="substring-after(@base, 'ξ.')"/>
        <xsl:variable name="target">
          <xsl:choose>
            <!-- "$", the formation itself -->
            <xsl:when test="@base='ξ'">
              <xsl:text>$</xsl:text>
            </xsl:when>
            <!-- "^", "^.^", and so on: one "^" per "ρ" hop -->
            <xsl:when test="matches(@base, '^ξ(\.ρ)+$')">
              <xsl:value-of select="replace($ref, 'ρ', '^')"/>
            </xsl:when>
            <!-- a sibling attribute of the same formation -->
            <xsl:when test="matches(@base, '^ξ\.[^.]+$') and ../o[@name=$ref]">
              <xsl:value-of select="$ref"/>
            </xsl:when>
          </xsl:choose>
        </xsl:variable>
        <xsl:if test="$target != ''">
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
            <xsl:value-of select="eo:escape(string($target))"/>
            <xsl:text> without adding anything, use </xsl:text>
            <xsl:value-of select="eo:escape(string($target))"/>
            <xsl:text> directly instead</xsl:text>
          </defect>
        </xsl:if>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
