<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" id="mandatory-spdx" version="2.0">
  <xsl:output encoding="UTF-8"/>
  <xsl:template match="/">
    <defects>
      <xsl:if test="count(/object/metas/meta[head ='spdx'])=0">
        <xsl:element name="defect">
          <xsl:attribute name="line">
            <xsl:value-of select="0"/>
          </xsl:attribute>
          <xsl:attribute name="severity">
            <xsl:text>warning</xsl:text>
          </xsl:attribute>
          <xsl:text>The +spdx meta is mandatory, but is absent</xsl:text>
        </xsl:element>
      </xsl:if>
    </defects>
  </xsl:template>
</xsl:stylesheet>
