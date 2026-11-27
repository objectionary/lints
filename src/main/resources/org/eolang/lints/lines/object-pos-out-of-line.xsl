<?xml version="1.0" encoding="UTF-8"?>
<!--
* SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
* SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" xmlns:xs="http://www.w3.org/2001/XMLSchema" id="object-pos-out-of-line" version="2.0">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <!--
  The lines of the listing. Without a listing, there is nothing to
  compare positions with, so nothing is reported.
  -->
  <xsl:variable name="lines" select="if (/object/listing) then tokenize(/object/listing, '&#10;') else ()" as="xs:string*"/>
  <!--
  The number of characters in a line. A character outside of the Basic
  Multilingual Plane, such as an emoji, is counted twice, because a
  producer may count positions in UTF-16 units. The check is thus never
  stricter than the producer was.
  -->
  <xsl:function name="eo:width" as="xs:integer">
    <xsl:param name="line" as="xs:string"/>
    <xsl:sequence select="string-length($line) + count(string-to-codepoints($line)[. gt 65535])"/>
  </xsl:function>
  <xsl:template match="/">
    <defects>
      <xsl:if test="exists($lines)">
        <xsl:for-each select="//o[@pos castable as xs:integer and @line castable as xs:integer]">
          <xsl:variable name="no" select="xs:integer(@line)"/>
          <xsl:variable name="pos" select="xs:integer(@pos)"/>
          <xsl:if test="$no ge 1 and $no le count($lines) and $pos ge eo:width($lines[$no])">
            <xsl:element name="defect">
              <xsl:attribute name="line">
                <xsl:value-of select="eo:lineno(@line)"/>
              </xsl:attribute>
              <xsl:attribute name="severity">
                <xsl:text>error</xsl:text>
              </xsl:attribute>
              <xsl:text>The position </xsl:text>
              <xsl:value-of select="$pos"/>
              <xsl:text> is out of the line </xsl:text>
              <xsl:value-of select="$no"/>
              <xsl:text>, which contains only </xsl:text>
              <xsl:value-of select="eo:width($lines[$no])"/>
              <xsl:text> characters</xsl:text>
            </xsl:element>
          </xsl:if>
        </xsl:for-each>
      </xsl:if>
    </defects>
  </xsl:template>
</xsl:stylesheet>
