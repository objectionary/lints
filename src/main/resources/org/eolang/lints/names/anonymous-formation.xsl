<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:eo="https://www.eolang.org" id="anonymous-formation" version="2.0">
  <xsl:import href="/org/eolang/parser/_funcs.xsl"/>
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:function name="eo:matches-any-void" as="xs:boolean">
    <xsl:param name="text" as="xs:string"/>
    <xsl:param name="patterns" as="xs:string*"/>
    <xsl:sequence select="some $pattern in $patterns satisfies matches($text, $pattern)"/>
  </xsl:function>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="//o[not(@name) and not(@base) and not(eo:has-data(.) and parent::o[@base='Q.org.eolang.bytes'])]">
        <xsl:variable name="allowed" as="xs:string*">
          <xsl:for-each select="o[@base='∅']">
            <xsl:sequence select="concat('\$(\.\^)+\.', @name, '(?:\.\w+)?')"/>
          </xsl:for-each>
        </xsl:variable>
        <xsl:for-each select=".//o[starts-with(@base, '$.^.') and not(eo:matches-any-void(@base, $allowed))]">
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
            <xsl:attribute name="severity">
              <xsl:text>warning</xsl:text>
            </xsl:attribute>
            <xsl:text>It is not recommended to have anonymous formation accessing undefined object </xsl:text>
            <xsl:value-of select="eo:escape(replace(@base, '^\$(\.\^)+\.', ''))"/>
            <xsl:text> inside the formation</xsl:text>
          </defect>
        </xsl:for-each>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
