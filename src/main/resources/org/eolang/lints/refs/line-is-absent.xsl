<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" id="line-is-absent" version="2.0">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <!--
  Here we go through all objects and find what their @base
  are referring to. If we find the object they refer to,
  everything is OK. If we don't, we report an error.
  -->
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:key name="objsNoLineByName" match="o[not(@line)]" use="@name"/>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="//o[@base and not(starts-with(@base, '.')) and @base!='ξ' and @base!='ρ']">
        <xsl:variable name="self" select="."/>
        <xsl:variable name="target" select="key('objsNoLineByName', $self/@base)"/>
        <xsl:if test="$target">
          <defect line="0" severity="error">
            The @line attribute is absent at <xsl:value-of select="$target/@name"/>
          </defect>
        </xsl:if>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
