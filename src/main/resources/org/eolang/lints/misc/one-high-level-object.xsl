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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" version="2.0" id="one-high-level-object">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:variable name="program" select="/program/@name"/>
  <xsl:variable name="tested" select="/program/metas/meta[head='tests']"/>
  <xsl:variable name="objects" select="count(/program/objects/o)"/>
  <xsl:template match="/">
    <defects>
      <xsl:if test="$objects&gt;1 and not($tested)">
        <defect>
          <xsl:attribute name="line">
            <xsl:value-of select="eo:lineno(@line)"/>
          </xsl:attribute>
          <xsl:attribute name="severity">warning</xsl:attribute>
          <xsl:text>Program </xsl:text>
          <xsl:value-of select="eo:escape($program)"/>
          <xsl:text> has </xsl:text>
          <xsl:value-of select="$objects"/>
          <xsl:text> objects, while only 1 is allowed</xsl:text>
        </defect>
      </xsl:if>
    </defects>
  </xsl:template>
</xsl:stylesheet>
