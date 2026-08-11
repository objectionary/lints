<?xml version="1.0" encoding="UTF-8"?>
<!--
* SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
* SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" xmlns:xs="http://www.w3.org/2001/XMLSchema" id="bad-test-name" version="2.0">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:import href="/org/eolang/funcs/test-name.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="/object//o[eo:test-name(@name)]">
        <!--
        A truthy test asserts that something works, thus it is a positive one,
        while a throwing test asserts that something fails, thus it is a
        negative one. Each kind has its own set of allowed prefixes.
        -->
        <xsl:variable name="positive" as="xs:boolean" select="starts-with(@name, '+')"/>
        <xsl:variable name="regexp" as="xs:string" select="if ($positive) then '^(can|accepts)-' else '^(cannot|rejects|stops-on)-'"/>
        <xsl:if test="not(matches(substring(@name, 2), $regexp))">
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
            <xsl:text>The name of the </xsl:text>
            <xsl:value-of select="if ($positive) then 'positive' else 'negative'"/>
            <xsl:text> test object </xsl:text>
            <xsl:value-of select="eo:escape(substring(@name, 2))"/>
            <xsl:text> must start with one of the prefixes </xsl:text>
            <xsl:value-of select="eo:escape(if ($positive) then 'can-, accepts-' else 'cannot-, rejects-, stops-on-')"/>
          </defect>
        </xsl:if>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
