<?xml version="1.0" encoding="UTF-8"?>
<!--
* SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
* SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" xmlns:xs="http://www.w3.org/2001/XMLSchema" id="misplaced-dataization" version="2.0">
  <xsl:import href="/org/eolang/parser/_funcs.xsl"/>
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:import href="/org/eolang/funcs/test-name.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <!--
  The name the formation goes by in the source, or the line it starts at
  when the source gives it no name: the parser names an anonymous
  formation after its position, with a cactus in the middle.
  -->
  <xsl:function name="eo:label" as="xs:string">
    <xsl:param name="o" as="element()"/>
    <xsl:variable name="name" select="($o/@local, $o/@name[not(contains(., '🌵'))])[1]"/>
    <xsl:sequence select="if ($name) then eo:escape($name) else concat('the formation at line ', eo:lineno($o/@line))"/>
  </xsl:function>
  <!--
  The cache of a "Φ.dataized" lives in the copy of the formation that
  holds it. When the formation is nested and the target is a plain chain
  of attribute reads through "ξ.ρ", every copy dataizes the same object of
  the enclosing formation again, though the enclosing formation could do
  it once for all of them. The parser rolls dispatches on a reference into
  its name, so such a chain is always one object without arguments; a
  literal or an application never is, and that keeps syscalls out, since
  each of them must run on every call. Tests are left alone, because
  their copies are not repeated.
  -->
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="//o[@base = 'Φ.dataized' and count(o) = 1 and o[empty(o) and (@base = 'ξ.ρ' or starts-with(@base, 'ξ.ρ.'))] and not(ancestor::o[eo:test-name(@name)])]">
        <xsl:variable name="formation" select="ancestor::o[eo:abstract(.)][1]"/>
        <xsl:variable name="outer" select="$formation/ancestor::o[eo:abstract(.)][1]"/>
        <xsl:if test="exists($outer)">
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
            <xsl:attribute name="experimental">true</xsl:attribute>
            <xsl:text>The dataization in </xsl:text>
            <xsl:value-of select="eo:label($formation)"/>
            <xsl:text> reads only objects outside of it and runs again in every copy, move it up into </xsl:text>
            <xsl:value-of select="eo:label($outer)"/>
          </defect>
        </xsl:if>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
