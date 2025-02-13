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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" id="application-duality" version="2.0">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="//o[@base]">
        <xsl:variable name="args">
          <xsl:choose>
            <xsl:when test="starts-with(@base, '.')">
              <xsl:copy-of select="o[position()&gt;1]"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:copy-of select="o"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
        <xsl:if test="count($args/*) != count($args/*[@as]) and count($args/*) != count($args/*[not(@as)])">
          <defect>
            <xsl:attribute name="line">
              <xsl:value-of select="eo:lineno(@line)"/>
            </xsl:attribute>
            <xsl:attribute name="severity">
              <xsl:text>critical</xsl:text>
            </xsl:attribute>
            <xsl:text>The </xsl:text>
            <xsl:choose>
              <xsl:when test="@name">
                <xsl:text>object </xsl:text>
                <xsl:value-of select="eo:escape(@name)"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:text>anonymous object</xsl:text>
              </xsl:otherwise>
            </xsl:choose>
            <xsl:text> has duality in the application, which is prohibited</xsl:text>
          </defect>
        </xsl:if>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
