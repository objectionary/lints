<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" xmlns:xs="http://www.w3.org/2001/XMLSchema" id="defect-context" version="2.0">
  <xsl:function name="eo:defect-context" as="xs:string">
    <xsl:param name="context"/>
    <xsl:value-of select="substring(serialize($context, map{'method':'xml'}), 1, 300)"/>
  </xsl:function>
</xsl:stylesheet>
