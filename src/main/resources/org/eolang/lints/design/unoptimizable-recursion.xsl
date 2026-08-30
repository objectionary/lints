<?xml version="1.0" encoding="UTF-8"?>
<!--
* SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
* SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" xmlns:xs="http://www.w3.org/2001/XMLSchema" id="unoptimizable-recursion" version="2.0">
  <xsl:import href="/org/eolang/parser/_funcs.xsl"/>
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <!--
  Is the object in a tail position of the expression rooted at $root? It is
  when every step from $root down to it is one of two: a branch of an ".if",
  which is the argument α0 or α1 and never the receiver, or the last element
  of the tuple a "seq" is applied to, which is the α1 of the "Φ.tuple"
  standing as the α0 of the "Φ.seq". Both answer exactly that element, so
  the value of the whole expression is the value of the object whenever the
  object is forced. This is the same test the compiler runs in
  "_recursion.xsl", where it decides what to turn into a loop.
  -->
  <xsl:function name="eo:tail" as="xs:boolean">
    <xsl:param name="o" as="element()"/>
    <xsl:param name="root" as="element()"/>
    <xsl:variable name="parent" as="element()?" select="$o/parent::o"/>
    <xsl:choose>
      <xsl:when test="$o is $root">
        <xsl:sequence select="true()"/>
      </xsl:when>
      <xsl:when test="empty($parent)">
        <xsl:sequence select="false()"/>
      </xsl:when>
      <xsl:when test="ends-with($parent/@base, '.if') and $o/@as = ('α0', 'α1')">
        <xsl:sequence select="eo:tail($parent, $root)"/>
      </xsl:when>
      <xsl:when test="$parent/@base = 'Φ.tuple' and $parent/@as = 'α0' and $parent/parent::o/@base = 'Φ.seq' and $o/@as = 'α1'">
        <xsl:sequence select="eo:tail($parent/parent::o, $root)"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:sequence select="false()"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:function>
  <!--
  @todo #1321:60min Name the shapes this lint still misses.
  The compiler also leaves the recursion alone when the self-call is
  spelled "Phi.name" instead of "^.name", when the formation is reached
  other than by a plain application, as a decoratee or as the receiver of
  a dispatch, when an attribute holding a self-call is read from outside
  the formation, as in "range", and when two formations call each other.
  Each of those is syntactic, so each can be found here and reported with
  a reason of its own, the way the four below are.
  -->
  <!--
  A nested formation that calls itself has its recursion turned into a Java
  loop, one copy of the object per step instead of one Java stack block, but
  only when the compiler recognises the shape. When it does not, the program
  still runs, only deeper and slower, and nothing says so at the call site.
  We say it here.
  -->
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="//o[eo:abstract(.) and @name and parent::o and o[@name='φ']]">
        <xsl:variable name="self" select="concat('ξ.ρ.', @name)"/>
        <xsl:variable name="root" select="o[@name='φ']"/>
        <xsl:variable name="calls" select=".//o[@base = $self or starts-with(@base, concat($self, '.'))]"/>
        <xsl:variable name="loops" select="($root | $root//o)[@base = $self and eo:tail(., $root)]"/>
        <xsl:if test="exists($calls) and empty($loops)">
          <defect>
            <xsl:variable name="line" select="eo:lineno($calls[1]/@line)"/>
            <xsl:attribute name="line">
              <xsl:value-of select="$line"/>
            </xsl:attribute>
            <xsl:if test="$line = '0'">
              <xsl:attribute name="context">
                <xsl:value-of select="eo:defect-context(.)"/>
              </xsl:attribute>
            </xsl:if>
            <xsl:attribute name="severity">warning</xsl:attribute>
            <xsl:attribute name="experimental">true</xsl:attribute>
            <xsl:text>The recursion in </xsl:text>
            <xsl:value-of select="eo:escape(@name)"/>
            <xsl:text> cannot be turned into a loop, </xsl:text>
            <xsl:choose>
              <xsl:when test="o[@name='λ']">
                <xsl:text>the formation is an atom</xsl:text>
              </xsl:when>
              <xsl:when test=".//o[contains(@base, 'φ')]">
                <xsl:text>its body reads its own φ</xsl:text>
              </xsl:when>
              <xsl:when test="$calls[starts-with(@base, concat($self, '.'))]">
                <xsl:text>the self-call is the receiver of a dispatch</xsl:text>
              </xsl:when>
              <xsl:otherwise>
                <xsl:text>no self-call sits in a tail position</xsl:text>
              </xsl:otherwise>
            </xsl:choose>
          </defect>
        </xsl:if>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
