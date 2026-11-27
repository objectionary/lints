<?xml version="1.0" encoding="UTF-8"?>
<!--
* SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
* SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" xmlns:xs="http://www.w3.org/2001/XMLSchema" id="unknown-rho-attribute" version="2.0">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:import href="/org/eolang/funcs/void-name.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <!--
  A reference like "^.^.foo" lands in XMIR as "ξ.ρ.ρ.foo": the object
  goes up by two formations and takes their attribute "foo". The
  formation it lands in is known statically, it is the formation that
  encloses the one where the reference is written, two levels up. When
  that formation has no attribute with such a name, the reference is
  broken. We stay silent when the formation has a decoratee, since the
  attribute may come from it, when the reference goes beyond the
  outermost formation, since we don't know what is there, and when
  any formation on the way has no name, since such a formation has
  no parent at all, or has a void "ρ", like in "[^ x]", since its
  parent is given from outside.
  -->
  <xsl:function name="eo:rho-hops" as="xs:integer">
    <xsl:param name="base" as="xs:string"/>
    <xsl:sequence select="string-length(replace($base, '^ξ((\.ρ)+).*$', '$1')) idiv 2"/>
  </xsl:function>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="//o[starts-with(@base, 'ξ.ρ.')]">
        <xsl:variable name="hops" select="eo:rho-hops(@base)"/>
        <xsl:variable name="attr" select="tokenize(@base, '\.')[$hops + 2]"/>
        <xsl:variable name="scopes" select="reverse(ancestor::o[not(@base)])"/>
        <xsl:variable name="target" select="$scopes[$hops + 1]"/>
        <xsl:if test="$attr and $target and empty($scopes[position() le $hops][not(@name) or o[@name = 'ρ']]) and empty($target/o[@name = ($attr, 'φ')])">
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
            <xsl:text>The reference </xsl:text>
            <xsl:value-of select="eo:escape(string-join((for $i in 1 to $hops return '^', eo:void-title($attr)), '.'))"/>
            <xsl:text> refers to the attribute </xsl:text>
            <xsl:value-of select="eo:escape(eo:void-title($attr))"/>
            <xsl:text>, which is absent in the formation </xsl:text>
            <xsl:value-of select="eo:escape(($target/@name, '?')[1])"/>
          </xsl:element>
        </xsl:if>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
