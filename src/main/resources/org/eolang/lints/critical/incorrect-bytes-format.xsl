<?xml version="1.0" encoding="UTF-8"?>
<!--
* SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
* SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" version="2.0" id="incorrect-bytes-format">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/">
    <defects>
      <xsl:apply-templates select="//text()[normalize-space()]/parent::o" mode="with-data"/>
    </defects>
  </xsl:template>
  <xsl:template match="o" mode="with-data">
    <xsl:variable name="bytes" select="normalize-space(string-join(text(), ''))"/>
    <!--
    Almost every data object in a program is a well-formed sequence of
    two or more bytes, such as "40-45-00-00". The regular expression
    below is the costliest part of the lint, thus such a sequence is
    accepted without it: when every hex digit is turned into "0", what
    is left must start with "00-", end with "-00", and contain neither
    two dashes in a row, nor "000", nor "-0-", which means every byte
    has exactly two digits. Anything else, including the empty bytes
    and a single byte, goes to the regular expression.
    -->
    <xsl:variable name="shape" select="translate($bytes, '0123456789ABCDEF', '0000000000000000')"/>
    <xsl:variable name="plain" select="translate($shape, '0-', '') = '' and starts-with($shape, '00-') and ends-with($shape, '-00') and not(contains($shape, '--')) and not(contains($shape, '000')) and not(contains($shape, '-0-'))"/>
    <xsl:if test="not($plain) and not(matches($bytes, '^(--|[0-9A-F]{2}(-|(-[0-9A-F]{2})+))$'))">
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
        <xsl:attribute name="severity">critical</xsl:attribute>
        <xsl:text>The format of bytes is incorrect: </xsl:text>
        <xsl:value-of select="eo:escape($bytes)"/>
      </defect>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>
