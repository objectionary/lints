<?xml version="1.0" encoding="UTF-8"?>
<!--
* SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
* SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" xmlns:xs="http://www.w3.org/2001/XMLSchema" version="2.0" id="long-live-object">
  <xsl:import href="/org/eolang/parser/_funcs.xsl"/>
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:import href="/org/eolang/funcs/test-name.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <!--
  Maximum number of source lines an attribute may stay alive between its
  declaration and the last line, in the same formation, that uses it.
  -->
  <xsl:variable name="max" select="5" as="xs:integer"/>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="//o[eo:abstract(.)]">
        <xsl:variable name="formation" select="."/>
        <xsl:for-each select="o[@name and @base and @base != '∅' and @name != 'φ' and not(eo:test-name(@name))]">
          <xsl:variable name="name" select="@name"/>
          <xsl:variable name="declared" select="number(eo:lineno(@line))"/>
          <xsl:variable name="usages" select="$formation//o[(@base = concat('ξ.', $name) or starts-with(@base, concat('ξ.', $name, '.'))) and not(ancestor::o[generate-id(.) != generate-id($formation) and eo:abstract(.)]) and not(ancestor-or-self::o[eo:test-name(@name)])]"/>
          <xsl:variable name="last" select="max($usages/number(eo:lineno(@line)))"/>
          <xsl:if test="$declared &gt; 0 and exists($usages) and $last - $declared &gt; $max">
            <defect>
              <xsl:attribute name="line">
                <xsl:value-of select="$declared"/>
              </xsl:attribute>
              <xsl:attribute name="severity">warning</xsl:attribute>
              <xsl:text>The object </xsl:text>
              <xsl:value-of select="eo:escape($name)"/>
              <xsl:text> is declared here, but is not used until line </xsl:text>
              <xsl:value-of select="$last"/>
              <xsl:text>, </xsl:text>
              <xsl:value-of select="$last - $declared"/>
              <xsl:text> lines away; move its declaration closer to where it's used</xsl:text>
            </defect>
          </xsl:if>
        </xsl:for-each>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
