<?xml version="1.0" encoding="UTF-8"?>
<!--
* SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
* SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" xmlns:xs="http://www.w3.org/2001/XMLSchema" id="redundant-object" version="2.0">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:import href="/org/eolang/funcs/test-name.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <!--
  A reference through the name of the object, e.g. "os.is-linux", is
  resolved by the parser into the absolute name of the object, the one it
  keeps in "@loc", followed by the attribute: "Φ.os.is-linux", or
  "Φ.com.example.os.is-linux" in a program with a package.
  -->
  <xsl:variable name="loc" select="/object/o[1]/@loc"/>
  <xsl:variable name="root" select="/object/o[1]"/>
  <!--
  The attribute named "name" of the formation "owner", as a key: the same
  name in another formation is another attribute.
  -->
  <xsl:function name="eo:attribute" as="xs:string">
    <xsl:param name="owner" as="element()?"/>
    <xsl:param name="name" as="xs:string?"/>
    <xsl:sequence select="concat(generate-id($owner), ' ', $name)"/>
  </xsl:function>
  <!--
  The attributes a chain of names reaches from the formation "owner": the
  first name is an attribute of "owner", and when that attribute is a
  formation, the next name is an attribute of that formation, and so on.
  So "size.width" reaches both the "size" of "owner" and the "width" of
  the formation "size".
  -->
  <xsl:function name="eo:chain" as="xs:string*">
    <xsl:param name="owner" as="element()?"/>
    <xsl:param name="names" as="xs:string*"/>
    <xsl:if test="exists($names)">
      <xsl:sequence select="eo:attribute($owner, $names[1])"/>
      <xsl:if test="exists($names[2])">
        <xsl:variable name="inner" select="$owner/o[@name = $names[1] and not(@base)][1]"/>
        <xsl:if test="exists($inner)">
          <xsl:sequence select="eo:chain($inner, subsequence($names, 2))"/>
        </xsl:if>
      </xsl:if>
    </xsl:if>
  </xsl:function>
  <!--
  The attributes a reference reaches. "ξ.total" reaches the "total" of the
  formation the reference sits in, and every "ρ" hop moves one formation
  up, so "ξ.ρ.total" reaches the "total" of the formation around it.
  A reference through the name of the object reaches an attribute of the
  top object. A reference that goes on through a formation by its name,
  e.g. "ξ.size.width", reaches the attribute of that formation as well.
  -->
  <xsl:function name="eo:reached" as="xs:string*">
    <xsl:param name="ref" as="element()"/>
    <xsl:variable name="base" select="string($ref/@base)"/>
    <xsl:choose>
      <xsl:when test="matches($base, '^ξ(?:\.ρ)*\.')">
        <xsl:variable name="hops" select="count(tokenize(replace($base, '^(ξ(?:\.ρ)*)\..*$', '$1'), '\.')) - 1"/>
        <xsl:sequence select="eo:chain($ref/ancestor::o[not(@base)][$hops + 1], tokenize(replace($base, '^ξ(?:\.ρ)*\.', ''), '\.'))"/>
      </xsl:when>
      <xsl:when test="exists($loc) and starts-with($base, concat($loc, '.'))">
        <xsl:sequence select="eo:chain($root, tokenize(substring-after($base, concat($loc, '.')), '\.'))"/>
      </xsl:when>
      <xsl:when test="matches($base, '^Φ\.[^.]+\.')">
        <xsl:sequence select="eo:chain($root, subsequence(tokenize($base, '\.'), 3))"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:sequence select="''"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:function>
  <!-- The attribute a named object is: the one of its own formation -->
  <xsl:function name="eo:declared" as="xs:string">
    <xsl:param name="o" as="element()"/>
    <xsl:sequence select="eo:attribute($o/ancestor::o[not(@base)][1], $o/@name)"/>
  </xsl:function>
  <xsl:key name="references" match="o[@base]" use="eo:reached(.)"/>
  <xsl:template match="/">
    <defects>
      <xsl:variable name="top" select="/object/o/generate-id()"/>
      <!--
      A unit test written as an application, such as "pos.eq 6 +> works",
      carries a base just like an ordinary named application, but nothing
      ever refers to a test by its name, so it may not be inlined anywhere.
      -->
      <xsl:for-each select="//o[generate-id() != $top and @name and @name != 'φ' and @base and @base != '∅' and not(@base='ξ' and @name='xi🌵') and not(eo:test-name(@name))]">
        <xsl:variable name="refs" select="key('references', eo:declared(.))"/>
        <xsl:variable name="in-recursive" select="some $r in $refs, $f in $r/ancestor::o[@name and not(@base)] satisfies exists(key('references', eo:declared($f)) intersect $f/descendant::o)"/>
        <xsl:variable name="self-alias-crosses-formation" select="@base = 'ξ' and (some $r in $refs satisfies matches($r/@base, '^ξ\.ρ\.'))"/>
        <xsl:variable name="candidate-name" select="@name"/>
        <xsl:variable name="self-used-via-chained-dot" select="@base = 'ξ' and exists(//o[@base = concat('.', $candidate-name)])"/>
        <xsl:if test="count($refs)=1 and not(@name and o[1]/@base = 'Φ.dataized') and not($in-recursive) and not($self-alias-crosses-formation) and not($self-used-via-chained-dot)">
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
              <xsl:text>warning</xsl:text>
            </xsl:attribute>
            <xsl:text>The object </xsl:text>
            <xsl:value-of select="eo:escape((@local, @name)[1])"/>
            <xsl:text> is redundant and may be inlined</xsl:text>
          </xsl:element>
        </xsl:if>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
