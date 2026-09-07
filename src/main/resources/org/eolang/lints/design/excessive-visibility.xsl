<?xml version="1.0" encoding="UTF-8"?>
<!--
* SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
* SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" id="excessive-visibility" version="2.0">
  <xsl:import href="/org/eolang/parser/_funcs.xsl"/>
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:import href="/org/eolang/funcs/test-name.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="//o[eo:abstract(.) and @name]">
        <xsl:variable name="tests" select="o[eo:test-name(@name)]"/>
        <xsl:if test="exists($tests)">
          <xsl:for-each select="o[@name and @base and @base != '∅' and @base != 'Φ.number' and @name != 'φ' and not(@local) and not(eo:test-name(@name))]">
            <xsl:variable name="method" select="@name"/>
            <!--
            A test refers to a sibling method either by calling it on
            itself, which the parser resolves into a "ξ.ρ."-prefixed chain
            of self-references, or by chaining a dotted call onto some
            other expression, e.g. "(phrase "x").multi-words", which the
            parser resolves into a "."-prefixed postfix base.
            -->
            <xsl:variable name="used" select="$tests//o[@base and matches(@base, concat('^(ξ(\.ρ)+\.|\.)', $method, '(\.|$)'))]"/>
            <xsl:if test="empty($used)">
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
                <xsl:text>The method </xsl:text>
                <xsl:value-of select="eo:escape($method)"/>
                <xsl:text> is public, but no unit test refers to it; obfuscate it with &gt;&gt; instead of &gt;</xsl:text>
              </xsl:element>
            </xsl:if>
          </xsl:for-each>
        </xsl:if>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
