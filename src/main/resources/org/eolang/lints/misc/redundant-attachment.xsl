<?xml version="1.0" encoding="UTF-8"?>
<!--
* SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
* SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" xmlns:xs="http://www.w3.org/2001/XMLSchema" id="redundant-attachment" version="2.0">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:import href="/org/eolang/funcs/outer-refs.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:key name="referenced-by-auto-name" match="o[@base]" use="if (matches(@base, '^ξ(?:\.ρ)*\.')) then tokenize(replace(@base, '^ξ(?:\.ρ)*\.', ''), '\.')[1] else ''"/>
  <!--
  The ".as-bytes" wrapper the parser builds around a nameless "!" const
  argument, such as the "m!" in "m.plus m!". Its auto-name comes from the
  parser, not from a "&gt;&gt;" in the source, so there is no suffix to delete
  and the warning could not be acted upon.
  -->
  <xsl:function name="eo:const-wrapper" as="xs:boolean">
    <xsl:param name="object" as="element()"/>
    <xsl:sequence select="$object/@base='.as-bytes' and exists($object/o[@base='Φ.dataized'])"/>
  </xsl:function>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="//o[@name and matches(@name, '^a🌵[0-9]+-[0-9]+$') and not(eo:const-wrapper(.))]">
        <xsl:variable name="refs" select="key('referenced-by-auto-name', @name)"/>
        <xsl:variable name="external" select="$refs except descendant::o"/>
        <!--
        Only an object whose body stays within itself can afford to lose the
        name: dropping the name inlines the object at the single place that
        refers to it, and a "ξ.ρ."-based reference reaching past its own voids
        into the enclosing scope would then be flagged by the
        "anonymous-formation" lint instead. See #1261.
        -->
        <xsl:if test="empty(eo:outer-refs(.)) and (count($refs)=0 or (count($refs)=1 and count($external)=1 and not($external[1]/o) and $external[1]/@as))">
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
            <xsl:text>This object doesn't need to be named, since its auto-generated name is never referenced</xsl:text>
          </xsl:element>
        </xsl:if>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
