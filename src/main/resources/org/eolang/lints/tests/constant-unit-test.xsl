<?xml version="1.0" encoding="UTF-8"?>
<!--
* SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
* SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" xmlns:xs="http://www.w3.org/2001/XMLSchema" id="constant-unit-test" version="2.0">
  <xsl:import href="/org/eolang/parser/_funcs.xsl"/>
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:import href="/org/eolang/funcs/test-name.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <!--
  TRUE if the object is a literal: "true", "false", or a number, a
  string, or bytes, which contain nothing but their data. Such an
  object is the same no matter what the code under test does.
  -->
  <xsl:function name="eo:literal" as="xs:boolean">
    <xsl:param name="o" as="element()"/>
    <xsl:sequence select="($o/@base = ('Φ.true', 'Φ.false') and not($o/o)) or ($o/@base = ('Φ.number', 'Φ.string', 'Φ.bytes') and $o/o and not($o//o[@base and @base != 'Φ.bytes']) and not($o//o[not(@base) and not(eo:has-data(.))]))"/>
  </xsl:function>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="/object//o[eo:test-name(@name) and eo:abstract(.)]/o[@name = 'φ' and eo:literal(.)]">
        <xsl:variable name="test" select="parent::o"/>
        <xsl:element name="defect">
          <xsl:variable name="line" select="eo:lineno(@line)"/>
          <xsl:attribute name="line">
            <xsl:value-of select="$line"/>
          </xsl:attribute>
          <xsl:if test="$line = '0'">
            <xsl:attribute name="context">
              <xsl:value-of select="eo:defect-context($test)"/>
            </xsl:attribute>
          </xsl:if>
          <xsl:attribute name="severity">
            <xsl:text>warning</xsl:text>
          </xsl:attribute>
          <xsl:text>The "@" attribute of the unit test </xsl:text>
          <xsl:value-of select="eo:escape(eo:test-title($test/@name))"/>
          <xsl:text> is a literal, which doesn't depend on any code, so the test checks nothing</xsl:text>
        </xsl:element>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
