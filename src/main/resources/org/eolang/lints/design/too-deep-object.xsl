<?xml version="1.0" encoding="UTF-8"?>
<!--
* SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
* SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" xmlns:xs="http://www.w3.org/2001/XMLSchema" version="2.0" id="too-deep-object">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <!--
  Depth of the object in its nesting chain, counting only the ancestors that
  represent real nesting: the wrapper objects ("Φ.tuple", "Φ.bytes",
  "Φ.number", "Φ.string") do not count. The selector and the message use the
  same metric.
  -->
  <xsl:function name="eo:depth" as="xs:integer">
    <xsl:param name="o" as="element()"/>
    <xsl:sequence select="count($o/ancestor::o[not(@base='Φ.tuple') and not(@base='Φ.bytes') and not(@base='Φ.number') and not(@base='Φ.string')])"/>
  </xsl:function>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="//o[eo:depth(.) &gt; 12 and not(.//o[eo:depth(.) &gt; 12])]">
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
          <xsl:text>The object </xsl:text>
          <xsl:value-of select="eo:escape(if (string(@name) != '') then @name else tokenize(@base, '\.')[last()])"/>
          <xsl:text> is nested too deep: </xsl:text>
          <xsl:value-of select="eo:depth(.)"/>
          <xsl:text> levels of nesting, while the maximum is 12</xsl:text>
        </defect>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
