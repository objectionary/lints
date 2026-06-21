<?xml version="1.0" encoding="UTF-8"?>
<!--
  * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
  * SPDX-License-Identifier: MIT
  -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:eo="https://www.eolang.org" id="compound-name" version="2.0">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:import href="/org/eolang/funcs/special-name.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <!--Since developers might decide not to use kebab-case, it's better to catch all such cases.-->
  <xsl:function name="eo:compound" as="xs:boolean">
    <xsl:param name="name"/>
    <xsl:sequence select="contains($name, '-') or contains($name, '_') or matches($name, '[A-Z]')"/>
  </xsl:function>
  <!--
    These prefixes/suffixes are idiomatic in EO standard library:
    - 'as-' for type conversions (as-bytes, as-i64, as-number, etc.)
    - 'is-' for boolean predicates (is-empty, is-nan, is-finite, etc.)
    - '-of' for extracting parts (slice-of, value-of, length-of, etc.)
  -->
  <xsl:function name="eo:idiomatic" as="xs:boolean">
    <xsl:param name="name"/>
    <xsl:sequence select="
      starts-with($name, 'as-') or
      starts-with($name, 'is-') or
      starts-with($name, 'has-') or
      starts-with($name, 'can-') or
      starts-with($name, 'should-') or
      starts-with($name, 'will-') or
      ends-with($name, '-of') or
      ends-with($name, '-from') or
      ends-with($name, '-to') or
      ends-with($name, '-with') or
      starts-with($name, 'sin') or
      starts-with($name, 'cos') or
      starts-with($name, 'tan') or
      starts-with($name, 'asin') or
      starts-with($name, 'acos') or
      starts-with($name, 'atan') or
      starts-with($name, 'sqrt') or
      starts-with($name, 'abs') or
      starts-with($name, 'exp') or
      starts-with($name, 'log') or
      $name = 'stdin' or
      $name = 'stdout' or
      $name = 'stderr' or
      $name = 'plus' or
      $name = 'minus' or
      $name = 'times' or
      $name = 'divided'
    "/>
  </xsl:function>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="//o[@base and @name and not(eo:special(@name)) and eo:compound(@name) and not(eo:idiomatic(@name))]">
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
          <xsl:text>Object name </xsl:text>
          <xsl:value-of select="eo:escape(@name)"/>
          <xsl:text> should not be compound unless it's formation</xsl:text>
        </defect>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
