<?xml version="1.0"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" xmlns:xs="http://www.w3.org/2001/XMLSchema" id="special-name" version="2.0">
  <xsl:import href="/org/eolang/parser/_specials.xsl"/>
  <xsl:function name="eo:special" as="xs:boolean">
    <xsl:param name="name"/>
    <xsl:sequence select="$name = '@' or $name = $eo:lambda or contains($name, $eo:cactoos)"/>
  </xsl:function>
</xsl:stylesheet>
