<?xml version="1.0" encoding="UTF-8"?>
<!--
* SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
* SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" version="2.0" id="test-with-comment">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:import href="/org/eolang/funcs/test-name.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="/object/comments/comment">
        <xsl:variable name="cline" select="number(@line)"/>
        <xsl:variable name="following" select="/object/o[number(@line) &gt; $cline]"/>
        <xsl:variable name="next" select="$following[number(@line) = min($following/number(@line))][1]"/>
        <xsl:if test="eo:test-name($next/@name)">
          <defect>
            <xsl:variable name="line" select="eo:lineno($next/@line)"/>
            <xsl:attribute name="line">
              <xsl:value-of select="$line"/>
            </xsl:attribute>
            <xsl:if test="$line = '0'">
              <xsl:attribute name="context">
                <xsl:value-of select="eo:defect-context($next)"/>
              </xsl:attribute>
            </xsl:if>
            <xsl:attribute name="severity">warning</xsl:attribute>
            <xsl:text>The test object </xsl:text>
            <xsl:value-of select="eo:escape($next/@name)"/>
            <xsl:text> has a comment, which duplicates its name. Make the name self-explanatory and remove the comment</xsl:text>
          </defect>
        </xsl:if>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
