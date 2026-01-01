<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" xmlns:xs="http://www.w3.org/2001/XMLSchema" id="escape" version="2.0">
  <xsl:function name="eo:escape" as="xs:string">
    <xsl:param name="text"/>
    <xsl:value-of select="concat('&quot;', replace(replace($text, ' ', '⌴'), '&quot;', '\\x22'), '&quot;')"/>
  </xsl:function>
</xsl:stylesheet>
