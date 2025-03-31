<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" id="missing-rho-reference" version="2.0">
  <xsl:import href="/org/eolang/parser/_funcs.xsl"/>
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <!--
   Recursively check if owner object is not auto named object, otherwise check is attribute is present.
   You can read more about auto named objects here: https://news.eolang.org/2025-02-21-auto-named-abstract-objects.html.
   @todo #13:45min Restructure `report-missing` template and its call in the root template.
    Currently, we are calling `report-missing` template from root template to do the job, while
    we should return only the parent object to which the rho attribute refers. Also, don't forget
    to rename the template to something like `rho-parent`.
  -->
  <xsl:template name="report-missing">
    <xsl:param name="position"/>
    <xsl:param name="attribute"/>
    <xsl:param name="owner"/>
    <xsl:choose>
      <xsl:when test="$owner[not(eo:abstract(.)) or @name='@'] or not(empty($owner/o[starts-with(@name, 'a🌵')]))">
        <xsl:call-template name="report-missing">
          <xsl:with-param name="position" select="$position + 1"/>
          <xsl:with-param name="attribute" select="$attribute"/>
          <xsl:with-param name="owner" select="ancestor::node()[$position + 1]"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:if test="not($owner/o[@name=$attribute])">
          <xsl:element name="defect">
            <xsl:variable name="line" select="eo:lineno(@line)"/>
            <xsl:attribute name="line">
              <xsl:value-of select="$line"/>
            </xsl:attribute>
            <xsl:if test="$line = '0'">
              <xsl:attribute name="context">
                <xsl:value-of select="eo:defect-context(.)"/>
              </xsl:attribute>
            </xsl:if>
            <xsl:attribute name="severity">
              <xsl:text>error</xsl:text>
            </xsl:attribute>
            <xsl:text>The rho reference "</xsl:text>
            <xsl:value-of select="$attribute"/>
            <xsl:text>" </xsl:text>
            <xsl:text>is missing in "</xsl:text>
            <xsl:value-of select="$owner/@name"/>
            <xsl:text>" object</xsl:text>
          </xsl:element>
        </xsl:if>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="//o[@base and starts-with(@base, '$.^')]">
        <xsl:variable name="position" select="(count(tokenize(@base, '\^')) - 1) * 2"/>
        <xsl:call-template name="report-missing">
          <xsl:with-param name="position" select="$position"/>
          <xsl:with-param name="attribute" select="translate(translate(@base, '.^', ''), '$', '')"/>
          <xsl:with-param name="owner" select="ancestor::node()[$position]"/>
        </xsl:call-template>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
