<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" xmlns:xs="http://www.w3.org/2001/XMLSchema" id="lineno" version="2.0">
  <xsl:function name="eo:lineno" as="xs:string">
    <xsl:param name="line"/>
    <xsl:sequence select="if ($line and number($line) = number($line)) then $line else '0'"/>
  </xsl:function>
</xsl:stylesheet>
