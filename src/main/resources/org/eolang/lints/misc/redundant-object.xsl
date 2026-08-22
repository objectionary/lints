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
  <xsl:key name="referenced-by-name" match="o[@base]" use="if (matches(@base, '^ξ(?:\.ρ)*\.')) then tokenize(replace(@base, '^ξ(?:\.ρ)*\.', ''), '\.')[1] else ''"/>
  <xsl:template match="/">
    <defects>
      <xsl:variable name="top" select="/object/o/generate-id()"/>
      <xsl:for-each select="//o[generate-id() != $top and @name and @name != 'φ' and @base and @base != '∅' and not(@base='ξ' and @name='xi🌵')]">
        <xsl:variable name="in-recursive" select="some $r in key('referenced-by-name', @name), $f in $r/ancestor::o[@name and not(@base)] satisfies exists(key('referenced-by-name', $f/@name) intersect $f/descendant::o)"/>
        <xsl:variable name="self-alias-crosses-formation" select="@base = 'ξ' and (some $r in key('referenced-by-name', @name) satisfies matches($r/@base, '^ξ\.ρ\.'))"/>
        <xsl:if test="count(key('referenced-by-name', @name))&lt;=1 and not(@name and o[1]/@base = 'Φ.dataized') and not($in-recursive) and not($self-alias-crosses-formation)">
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
            <xsl:text>The object </xsl:text>
            <xsl:value-of select="eo:escape(@name)"/>
            <xsl:text> is redundant and may be inlined</xsl:text>
          </xsl:element>
        </xsl:if>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
