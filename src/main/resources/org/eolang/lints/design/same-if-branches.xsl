<?xml version="1.0" encoding="UTF-8"?>
<!--
* SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
* SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" xmlns:xs="http://www.w3.org/2001/XMLSchema" version="2.0" id="same-if-branches">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <!--
  TRUE when the two objects have the same structure: the same base, the
  same name, the same leaf text (when both are leaves), and the same
  children, pairwise, in the same order. Positional attributes that only
  record where a node sits in the source ("line", "loc", "pos", "as") are
  ignored, so two branches built from the same code, but copy-pasted onto
  different lines or bound to different attributes, still compare equal.
  -->
  <xsl:function name="eo:same" as="xs:boolean">
    <xsl:param name="a" as="element()"/>
    <xsl:param name="b" as="element()"/>
    <xsl:variable name="kids-a" select="$a/o"/>
    <xsl:variable name="kids-b" select="$b/o"/>
    <xsl:sequence select="((not($a/@base) and not($b/@base)) or string($a/@base) = string($b/@base)) and ((not($a/@name) and not($b/@name)) or string($a/@name) = string($b/@name)) and count($kids-a) = count($kids-b) and (count($kids-a) &gt; 0 or normalize-space($a) = normalize-space($b)) and (every $i in 1 to count($kids-a) satisfies eo:same($kids-a[$i], $kids-b[$i]))"/>
  </xsl:function>
  <!--
  An "if" whose two branches, "α0" (taken when the condition is true) and
  "α1" (taken when it is false), run exactly the same code makes the
  condition pointless: the result is the same either way. The shared code
  should be moved above the "if" instead of being duplicated in both
  branches.
  -->
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="//o[ends-with(@base, '.if') and o[@as='α0'] and o[@as='α1'] and eo:same(o[@as='α0'], o[@as='α1'])]">
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
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
