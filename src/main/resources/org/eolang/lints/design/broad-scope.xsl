<?xml version="1.0" encoding="UTF-8"?>
<!--
* SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
* SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" version="2.0" id="broad-scope">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:key name="referenced-by-name" match="o[@base]" use="if (matches(@base, '^ξ(?:\.ρ)*\.')) then tokenize(replace(@base, '^ξ(?:\.ρ)*\.', ''), '\.')[1] else ''"/>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="//o[@local and @name and not(@name='φ')]">
        <xsl:variable name="parent" select="parent::o"/>
        <xsl:variable name="refs" select="key('referenced-by-name', @name)"/>
        <xsl:variable name="containers" select="$refs/ancestor::o[parent::o[generate-id() = generate-id($parent)]][1]"/>
        <xsl:if test="count($refs) &gt; 0 and count($containers) = count($refs) and count(distinct-values($containers/generate-id())) = 1 and not(exists($containers[generate-id() = generate-id(current())]))">
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
            <xsl:text>The private attribute </xsl:text>
            <xsl:value-of select="eo:escape(@local)"/>
            <xsl:text> is used only inside the object </xsl:text>
            <xsl:value-of select="eo:escape(($containers[1]/@local, $containers[1]/@name)[1])"/>
            <xsl:text>, its scope is too broad</xsl:text>
          </xsl:element>
        </xsl:if>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
