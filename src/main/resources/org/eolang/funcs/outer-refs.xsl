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
  The objects a formation reads from the scope that encloses it: every
  "ξ.ρ."-based reference in its body that neither its own voids nor its own
  bound attributes cover. When this is empty, the formation stands on its
  own and makes sense without a name and without an enclosing scope. Both
  "anonymous-formation" and "redundant-attachment" judge a formation by
  this very set, from opposite sides, so they share it here.
  -->
  <xsl:function name="eo:outer-refs" as="element()*">
    <xsl:param name="formation" as="element()"/>
    <xsl:variable name="allowed" as="xs:string*">
      <xsl:for-each select="$formation/o[@base='∅']">
        <xsl:sequence select="concat('ξ(\.ρ)+\.', @name, '(?:\.\w+)?')"/>
      </xsl:for-each>
    </xsl:variable>
    <xsl:for-each select="$formation//o[starts-with(@base, 'ξ.ρ.') and not(some $pattern in $allowed satisfies matches(@base, $pattern))]">
      <xsl:variable name="name" select="eo:base-to-name(@base)"/>
      <xsl:if test="not($formation/o[@name = $name])">
        <xsl:sequence select="."/>
      </xsl:if>
    </xsl:for-each>
  </xsl:function>
</xsl:stylesheet>
