<?xml version="1.0" encoding="UTF-8"?>
<!--
* SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
* SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" xmlns:xs="http://www.w3.org/2001/XMLSchema" id="outer-refs" version="2.0">
  <!--
  The name a "ξ.ρ."-based reference resolves to, with the "ξ" and the
  chain of "ρ" hops that lead out of the formation stripped away.
  -->
  <xsl:function name="eo:base-to-name" as="xs:string">
    <xsl:param name="base" as="xs:string"/>
    <xsl:value-of select="replace($base, '^ξ(\.ρ)+\.', '')"/>
  </xsl:function>
  <!--
  The number of "ρ" hops in a "ξ.ρ."-based reference. A reference with
  fewer hops than the formations between it and the checked formation
  points at one of those inner formations, so it never leaves the checked one.
  -->
  <xsl:function name="eo:hops" as="xs:integer">
    <xsl:param name="base" as="xs:string"/>
    <xsl:sequence select="count(tokenize(replace($base, '^(ξ(\.ρ)+)\..*$', '$1'), '\.')) - 1"/>
  </xsl:function>
  <!--
  The objects a formation reads from the scope that encloses it: every
  "ξ.ρ."-based reference in its body that neither its own voids nor its own
  bound attributes cover. When this is empty, the formation stands on its
  own and makes sense without a name and without an enclosing scope. Both
  "anonymous-formation" and "redundant-attachment" judge a formation by
  this very set, from opposite sides, so they share it here.
  -->
  <xsl:function name="eo:outer-refs" as="element()*">
    <xsl:param name="formation" as="element()"/>
    <!--
    Every attribute of the formation, a void or a bound one, also covers
    a dispatch on it, such as "^.base.plus x" in a nested formation, which
    the parser keeps as the single reference "ξ.ρ.base.plus".
    -->
    <xsl:variable name="allowed" as="xs:string*">
      <xsl:for-each select="$formation/o[@name]">
        <xsl:sequence select="concat(@name, '.')"/>
      </xsl:for-each>
    </xsl:variable>
    <!--
    Only a reference whose hops end right at the formation can be covered by
    its attributes. One with more hops goes past the formation, so it reads
    the enclosing scope even when the formation has an attribute of the same
    name, such as "^.x" written right in the body of "[acc x]".
    -->
    <xsl:for-each select="$formation//o[starts-with(@base, 'ξ.ρ.')]">
      <xsl:variable name="inner" select="count(ancestor::o[not(@base) and exists(. intersect $formation/descendant-or-self::o)]) - 1"/>
      <xsl:variable name="hops" select="eo:hops(@base)"/>
      <xsl:variable name="name" select="eo:base-to-name(@base)"/>
      <xsl:if test="$hops gt $inner or ($hops eq $inner and not(some $prefix in $allowed satisfies starts-with($name, $prefix)) and not($formation/o[@name = $name]))">
        <xsl:sequence select="."/>
      </xsl:if>
    </xsl:for-each>
  </xsl:function>
</xsl:stylesheet>
