<?xml version="1.0" encoding="UTF-8"?>
<!--
* SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
* SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" xmlns:xs="http://www.w3.org/2001/XMLSchema" version="2.0" id="cyclomatic-complexity">
  <xsl:import href="/org/eolang/parser/_funcs.xsl"/>
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <!--
  EO has no loops or "goto", so the only decision points a formation can
  have are a standalone "while" and an "if"/"and"/"or" dispatched on
  something. Whether that something is really a boolean, as opposed to a
  third-party object that happens to have a method with the same name, is
  not decidable without type inference, so this counts any dispatch whose
  last "."-separated segment is one of the three names. It is a heuristic,
  the same way "broad-scope" and "sparse-decoration" are, which is why the
  defect it produces is marked experimental.
  -->
  <xsl:function name="eo:decision" as="xs:boolean">
    <xsl:param name="base" as="xs:string?"/>
    <xsl:sequence select="exists($base) and ($base = 'Φ.while' or tokenize($base, '\.')[last()] = ('if', 'and', 'or'))"/>
  </xsl:function>
  <!--
  The formation a decision point counts against: its nearest enclosing
  formation. A decision inside a nested formation counts for that nested
  formation and not for the one around it, the same way a Java method's
  cyclomatic complexity does not include the branches of a method it calls.
  -->
  <xsl:function name="eo:owner" as="element()?">
    <xsl:param name="o" as="element()"/>
    <xsl:sequence select="$o/ancestor::o[eo:abstract(.) and o[@name='φ']][1]"/>
  </xsl:function>
  <xsl:variable name="eo:max" as="xs:integer" select="10"/>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="//o[eo:abstract(.) and @name and not(contains(@name, '🌵')) and o[@name='φ']]">
        <xsl:variable name="this" select="."/>
        <xsl:variable name="decisions" select=".//o[@base and eo:decision(string(@base)) and generate-id(eo:owner(.)) = generate-id($this)]"/>
        <xsl:variable name="complexity" select="count($decisions) + 1"/>
        <xsl:if test="$complexity &gt; $eo:max">
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
            <xsl:attribute name="experimental">
              <xsl:text>true</xsl:text>
            </xsl:attribute>
            <xsl:text>The object </xsl:text>
            <xsl:value-of select="eo:escape(@name)"/>
            <xsl:text> has a cyclomatic complexity of </xsl:text>
            <xsl:value-of select="$complexity"/>
            <xsl:text>, while the maximum is </xsl:text>
            <xsl:value-of select="$eo:max"/>
          </xsl:element>
        </xsl:if>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
