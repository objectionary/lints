<?xml version="1.0" encoding="UTF-8"?>
<!--
The MIT License (MIT)

Copyright (c) 2016-2024 Objectionary.com

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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" id="incorrect-node-rt-location" version="2.0">
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="/program/metas/meta">
        <xsl:variable name="meta-head" select="head"/>
        <xsl:variable name="meta-tail" select="tail"/>
        <xsl:variable name="runtime" select="normalize-space(substring-before(concat($meta-tail, ' '), ' '))"/>
        <xsl:variable name="location" select="normalize-space(substring-after($meta-tail, ' '))"/>
        <xsl:if test="$meta-head='rt' and count(part) &gt; 1 and $runtime='node' and not(matches($location, '^([a-zA-Z0-9_.-]+):(\d+\.\d+\.\d+)$'))">
          <xsl:element name="defect">
            <xsl:attribute name="line">
              <xsl:value-of select="if (@line) then @line else '0'"/>
            </xsl:attribute>
            <xsl:attribute name="severity">
              <xsl:text>warning</xsl:text>
            </xsl:attribute>
            <xsl:text>The format of the location of node runtime is wrong: "</xsl:text>
            <xsl:value-of select="$location"/>
            <xsl:text>"</xsl:text>
          </xsl:element>
        </xsl:if>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
