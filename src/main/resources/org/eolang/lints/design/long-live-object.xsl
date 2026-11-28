<?xml version="1.0" encoding="UTF-8"?>
<!--
* SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
* SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" xmlns:xs="http://www.w3.org/2001/XMLSchema" version="2.0" id="long-live-object">
  <xsl:import href="/org/eolang/parser/_funcs.xsl"/>
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:import href="/org/eolang/funcs/test-name.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <!--
  Maximum number of source lines an attribute may stay alive between its
  declaration and the last line, in the same formation, that uses it.
  -->
  <xsl:variable name="max" select="5" as="xs:integer"/>
  <!--
  The last line the declaration "$o" takes. An argument bound with ">>"
  is not a descendant of the declaration: the parser hoists it into a
  sibling attribute, such as "a🌵5-4", and leaves only a "ξ.a🌵5-4"
  reference in its place. Every such reference is followed to the
  hoisted sibling, recursively, unless that sibling sits on a line
  before "$declared", being first used elsewhere.
  -->
  <xsl:function name="eo:ends" as="xs:double">
    <xsl:param name="o" as="element()"/>
    <xsl:param name="formation" as="element()"/>
    <xsl:param name="declared" as="xs:double"/>
    <xsl:param name="seen" as="xs:string*"/>
    <xsl:variable name="hoisted" select="$formation/o[matches(@name, '^a🌵[0-9]+-[0-9]+$') and not(@name = $seen) and number(eo:lineno(@line)) &gt;= $declared]"/>
    <xsl:variable name="bases" select="$o/descendant-or-self::o/@base"/>
    <xsl:variable name="next" select="$hoisted[some $b in $bases satisfies ($b = concat('ξ.', @name) or starts-with($b, concat('ξ.', @name, '.')))]"/>
    <xsl:variable name="more" select="($seen, $next/@name)"/>
    <xsl:sequence select="max(($o/descendant-or-self::o/number(eo:lineno(@line)), for $n in $next return eo:ends($n, $formation, $declared, $more)))"/>
  </xsl:function>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="//o[eo:abstract(.)]">
        <xsl:variable name="formation" select="."/>
        <xsl:variable name="attributes" select="o[@name and @base and @base != '∅' and @name != 'φ' and not(eo:test-name(@name))]"/>
        <!--
        The printer sorts the attributes by the names they are written
        with, so an object bound with ">>" counts by its "@local" name,
        not by the synthetic "@name" the parser gives it.
        -->
        <xsl:variable name="names" select="for $a in $attributes return string(($a/@local, $a/@name)[1])"/>
        <xsl:variable name="sorted" select="every $i in 2 to count($names) satisfies $names[$i - 1] le $names[$i]"/>
        <xsl:for-each select="$attributes[not($sorted)]">
          <xsl:variable name="name" select="@name"/>
          <xsl:variable name="declared" select="number(eo:lineno(@line))"/>
          <!--
          The arguments of a vertical application sit on the lines below
          its name, and those lines are the declaration itself, not lines
          between the declaration and its usages: the distance is counted
          from the last line the declaration takes, together with the
          lines of the arguments it binds with ">>".
          -->
          <xsl:variable name="ends" select="eo:ends(., $formation, $declared, string(@name))"/>
          <xsl:variable name="usages" select="$formation//o[(@base = concat('ξ.', $name) or starts-with(@base, concat('ξ.', $name, '.'))) and not(ancestor::o[. &gt;&gt; $formation and eo:abstract(.)]) and not(ancestor-or-self::o[eo:test-name(@name)])]"/>
          <xsl:variable name="last" select="max($usages/number(eo:lineno(@line)))"/>
          <xsl:if test="$declared &gt; 0 and exists($usages) and $last - $ends &gt; $max">
            <defect>
              <xsl:attribute name="line">
                <xsl:value-of select="$declared"/>
              </xsl:attribute>
              <xsl:attribute name="severity">warning</xsl:attribute>
              <xsl:text>The object </xsl:text>
              <xsl:value-of select="eo:escape((@local, $name)[1])"/>
              <xsl:text> is declared here, but is not used until line </xsl:text>
              <xsl:value-of select="$last"/>
              <xsl:text>, </xsl:text>
              <xsl:value-of select="$last - $declared"/>
              <xsl:text> lines away; move its declaration closer to where it's used</xsl:text>
            </defect>
          </xsl:if>
        </xsl:for-each>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
