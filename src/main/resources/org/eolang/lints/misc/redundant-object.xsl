<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" id="redundant-object" version="2.0">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/">
    <xsl:variable name="eligible" select="//o[@name and @name!='φ' and @base and @base!='∅' and not(@base='ξ' and @name='xi🌵')]"/>
    <xsl:variable name="ref-groups" as="element(group)*">
      <xsl:for-each-group select="//o[matches(@base, '^ξ(?:\.ρ)*\.')]" group-by="replace(@base, '^ξ(?:\.ρ)*\.([^.\s]+).*', '$1')">
        <group name="{current-grouping-key()}" count="{count(current-group())}"/>
      </xsl:for-each-group>
    </xsl:variable>
    <defects>
      <xsl:variable name="top" select="/object/o/generate-id()"/>
      <xsl:for-each select="$eligible[generate-id() != $top]">
        <xsl:variable name="refs" select="number(($ref-groups[@name = current()/@name]/@count, 0)[1])"/>
        <xsl:if test="$refs &lt;= 1 and not(@name and o[1]/@base = 'Φ.org.eolang.dataized')">
          <defect line="{eo:lineno(@line)}" severity="warning">
            <xsl:if test="eo:lineno(@line) = '0'">
              <xsl:attribute name="context" select="eo:defect-context(.)"/>
            </xsl:if>
            <xsl:text>The object </xsl:text>
            <xsl:value-of select="eo:escape(@name)"/>
            <xsl:text> is redundant and may be inlined</xsl:text>
          </defect>
        </xsl:if>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
