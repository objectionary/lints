<?xml version="1.0" encoding="UTF-8"?>
<!--
  * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
  * SPDX-License-Identifier: MIT
  -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:eo="https://www.eolang.org" id="compound-attribute-name" version="2.0">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:import href="/org/eolang/funcs/special-name.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <!--Since developers might decide not to use kebab-case, it’s better to catch all such cases.-->
  <xsl:function name="eo:compound" as="xs:boolean">
    <xsl:param name="name"/>
    <xsl:sequence select="contains($name, '-') or contains($name, '_') or matches($name, '[A-Z]')"/>
  </xsl:function>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="//o[@base and @name and not(eo:special(@name)) and eo:compound(@name)]">
        <defect>
          <xsl:variable name="line" select="eo:lineno(@line)"/>
          <xsl:attribute name="line">
            <xsl:value-of select="$line"/>
          </xsl:attribute>
          <xsl:if test="$line = '0'">
            <xsl:attribute name="context">
              <xsl:value-of select="eo:defect-context(.)"/>
            </xsl:attribute>
          </xsl:if>
          <xsl:attribute name="severity">warning</xsl:attribute>
          <xsl:text>Object name "</xsl:text>
          <xsl:value-of select="@name"/>
          <xsl:text>" should not be compound unless it's formation</xsl:text>
        </defect>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
