<?xml version="1.0" encoding="UTF-8"?>
<!--
* SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
* SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" xmlns:xs="http://www.w3.org/2001/XMLSchema" version="2.0" id="same-if-branches">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:import href="/org/eolang/funcs/void-name.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <!--
  TRUE when the two objects have the same structure: the same base, the
  same name, the same leaf text (when both are leaves), and the same
  children, pairwise, in the same order. Positional attributes that only
  record where a node sits in the source ("line", "loc", "pos") are
  ignored. The outer branch bindings are ignored, while argument bindings
  inside the branches remain part of their structure.
  -->
  <xsl:function name="eo:same" as="xs:boolean">
    <xsl:param name="a" as="element()"/>
    <xsl:param name="b" as="element()"/>
    <xsl:variable name="kids-a" select="$a/o"/>
    <xsl:variable name="kids-b" select="$b/o"/>
    <xsl:sequence select="((not($a/@base) and not($b/@base)) or string($a/@base) = string($b/@base)) and ((not($a/@name) and not($b/@name)) or string($a/@name) = string($b/@name)) and count($kids-a) = count($kids-b) and (count($kids-a) &gt; 0 or normalize-space($a) = normalize-space($b)) and (every $i in 1 to count($kids-a) satisfies (string($kids-a[$i]/@as) = string($kids-b[$i]/@as) and eo:same($kids-a[$i], $kids-b[$i])))"/>
  </xsl:function>
  <!--
  The branches of an "if" can be passed positionally ("α0" and "α1") or
  by name ("left" and "right"). Compare the code after finding both branches.
  -->
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="//o[ends-with(@base, '.if')]">
        <xsl:variable name="left" select="o[eo:void-title(@as) = ('α0', 'left')]"/>
        <xsl:variable name="right" select="o[eo:void-title(@as) = ('α1', 'right')]"/>
        <xsl:if test="if (count($left) = 1 and count($right) = 1) then eo:same($left, $right) else false()">
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
            <xsl:text>The two branches of this "if" run the same code, move it above the "if" instead</xsl:text>
          </defect>
        </xsl:if>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
