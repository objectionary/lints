<?xml version="1.0" encoding="UTF-8"?>
<!--
The MIT License (MIT)

Copyright (c) 2016-2025 Objectionary.com

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included
in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" id="unknown-rt" version="2.0">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="/program/metas/meta">
        <xsl:variable name="meta-head" select="head"/>
        <xsl:variable name="meta-tail" select="tail"/>
        <xsl:variable name="allowed" select="('jvm', 'node')"/>
        <xsl:variable name="first">
          <xsl:choose>
            <xsl:when test="contains($meta-tail, ' ')">
              <xsl:value-of select="substring-before($meta-tail, ' ')"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="$meta-tail"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
        <xsl:if test="$meta-head='rt' and count(part) &gt; 0 and not($first = $allowed)">
          <xsl:element name="defect">
            <xsl:attribute name="line">
              <xsl:value-of select="eo:lineno(@line)"/>
            </xsl:attribute>
            <xsl:attribute name="severity">
              <xsl:text>critical</xsl:text>
            </xsl:attribute>
            <xsl:text>The runtime </xsl:text>
            <xsl:value-of select="eo:escape($first)"/>
            <xsl:text> is not supported (only </xsl:text>
            <xsl:for-each select="$allowed">
              <xsl:if test="position() &gt; 1">
                <xsl:text>, </xsl:text>
              </xsl:if>
              <xsl:value-of select="eo:escape(.)"/>
            </xsl:for-each>
            <xsl:text>)</xsl:text>
          </xsl:element>
        </xsl:if>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
