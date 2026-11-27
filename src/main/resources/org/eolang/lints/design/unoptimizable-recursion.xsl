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
  <xsl:import href="/org/eolang/funcs/test-name.xsl"/>
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
  The sibling formations the given formation calls through its receiver,
  as "^.G", which the parser resolves to "xi.rho.G"; a bare "G" naming a
  sibling is a parse error, so this is the only spelling. Only calls in
  the formation's own body count, not those in its nested formations or
  in its tests, and the formation itself is never its own callee here:
  a self-call is the business of the main template.
  -->
  <xsl:function name="eo:callees" as="element()*">
    <xsl:param name="f" as="element()"/>
    <xsl:variable name="body" select="$f//o[@base and ancestor::o[eo:abstract(.)][1] is $f and not(ancestor-or-self::o[eo:test-name(@name)])]"/>
    <xsl:sequence select="$f/../o[eo:abstract(.) and not(. is $f) and not(eo:test-name(@name)) and (some $n in (@name, @local)/string() satisfies $body[@base = concat('ξ.ρ.', $n) or starts-with(@base, concat('ξ.ρ.', $n, '.'))])]"/>
  </xsl:function>
  <!--
  All formations reachable from the given ones through "eo:callees", the
  given ones included. The $seen set guards against cycles.
  -->
  <xsl:function name="eo:reachable" as="element()*">
    <xsl:param name="todo" as="element()*"/>
    <xsl:param name="seen" as="element()*"/>
    <xsl:variable name="next" select="(for $f in $todo return eo:callees($f)) except ($seen | $todo)"/>
    <xsl:choose>
      <xsl:when test="empty($next)">
        <xsl:sequence select="$seen | $todo"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:sequence select="eo:reachable($next, $seen | $todo)"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:function>
  <!--
  The first sibling the given formation calls that calls it back, maybe
  through a longer chain of siblings: mutual recursion. The compiler turns
  only a formation's call to itself into a loop, so such a cycle always
  runs as plain recursion, however its calls are placed.
  -->
  <xsl:function name="eo:mutual" as="element()?">
    <xsl:param name="f" as="element()"/>
    <xsl:sequence select="(eo:callees($f)[some $g in eo:reachable(., ()) satisfies $g is $f])[1]"/>
  </xsl:function>
  <!--
  The package of the program, from its "+package" meta, or an empty string
  when it has none.
  -->
  <xsl:variable name="package" select="string((/object/metas/meta[head='package']/part[1])[1])" as="xs:string"/>
  <!--
  The spellings the parser gives to the plain name of an object: "Phi.<name>"
  in a program without a package, and "Phi.<package>.<name>" in a program
  with one, where the parser puts its top-level object into the package.
  -->
  <xsl:function name="eo:plain" as="xs:string+">
    <xsl:param name="name" as="xs:string"/>
    <xsl:sequence select="concat('Φ.', $name), if ($package = '') then () else concat('Φ.', $package, '.', $name)"/>
  </xsl:function>
  <!--
  TRUE when the @base is a self-call of the object with the given name, in
  any of the three forms the parser emits: the plain name resolves to
  "Phi.<name>" or "Phi.<package>.<name>", the "^" form to "xi.rho.<name>",
  and the "$" form to "xi.<name>". A dispatch on the self-call is also a
  self-call.
  -->
  <xsl:function name="eo:is-self-call" as="xs:boolean">
    <xsl:param name="base" as="xs:string"/>
    <xsl:param name="name" as="xs:string"/>
    <xsl:sequence select="$base = eo:plain($name) or (some $plain in eo:plain($name) satisfies starts-with($base, concat($plain, '.'))) or $base = concat('ξ.ρ.', $name) or starts-with($base, concat('ξ.ρ.', $name, '.')) or $base = concat('ξ.', $name) or starts-with($base, concat('ξ.', $name, '.'))"/>
  </xsl:function>
  <!--
  TRUE when the @base is a dispatch on a self-call, i.e. the self-call is
  the receiver and a method is dispatched on it.
  -->
  <xsl:function name="eo:is-self-dispatch" as="xs:boolean">
    <xsl:param name="base" as="xs:string"/>
    <xsl:param name="name" as="xs:string"/>
    <xsl:sequence select="(some $plain in eo:plain($name) satisfies starts-with($base, concat($plain, '.'))) or starts-with($base, concat('ξ.ρ.', $name, '.')) or starts-with($base, concat('ξ.', $name, '.'))"/>
  </xsl:function>
  <!--
  TRUE when the @base is a self-call in a tail position through the
  receiver, the "^.name" spelling of "xi.rho.<name>". This is the only
  spelling "recursion-to-loop.xsl" turns into a loop, in the compiler:
  "$self = concat('xi.rho.', @name)", matched against a call in a tail
  position of φ. A call spelled "Phi.name" or "xi.<name>", even in a tail
  position, still grows the Java stack, and so does a dispatch on it.
  -->
  <xsl:function name="eo:is-self-tail" as="xs:boolean">
    <xsl:param name="base" as="xs:string"/>
    <xsl:param name="name" as="xs:string"/>
    <xsl:sequence select="$base = concat('ξ.ρ.', $name)"/>
  </xsl:function>
  <!--
  TRUE when the @base is a self-call spelled without the receiver: the
  bare name, which the parser resolves to "Phi.<name>" or, in a program
  with a package, to "Phi.<package>.<name>" for a top-level formation, or
  to a same-named attribute reachable from a nested one, or the "$" form,
  "xi.<name>". "recursion-to-loop.xsl" only ever marks "xi.rho.<name>"
  with "@loop", so neither spelling is turned into a loop, in a tail
  position or not.
  -->
  <xsl:function name="eo:is-self-direct" as="xs:boolean">
    <xsl:param name="base" as="xs:string"/>
    <xsl:param name="name" as="xs:string"/>
    <xsl:sequence select="$base = eo:plain($name) or $base = concat('ξ.', $name)"/>
  </xsl:function>
  <!--
  A nested formation that calls itself has its recursion turned into a Java
  loop, one copy of the object per step instead of one Java stack block, but
  only when the compiler recognises the shape. When it does not, the program
  still runs, only deeper and slower, and nothing says so at the call site.
  We say it here.
  -->
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="//o[eo:abstract(.) and (@local or (@name and not(contains(@name, '🌵')))) and o[@name='φ']]">
        <xsl:variable name="name" select="string(@name)"/>
        <xsl:variable name="names" select="(@name, @local)/string()" as="xs:string+"/>
        <xsl:variable name="root" select="o[@name='φ']"/>
        <!-- A bare name can resolve to an attribute of the current formation. -->
        <xsl:variable name="shadowed" select="o[@name = $names]/@name"/>
        <xsl:variable name="calls" select=".//o[(some $n in $names satisfies eo:is-self-call(string(@base), $n)) and not(starts-with(@base, 'ξ.') and tokenize(@base, '\.')[2] = $shadowed) and not(ancestor::o[eo:test-name(@name)]) and ancestor::o[eo:abstract(.)][1] is current()]"/>
        <xsl:variable name="loops" select="($root | $root//o)[eo:is-self-tail(string(@base), $name) and eo:tail(., $root)]"/>
        <xsl:variable name="direct" select="$calls[(some $n in $names satisfies eo:is-self-direct(string(@base), $n)) and eo:tail(., $root)]"/>
        <xsl:variable name="elsewhere" select="$calls except ($root/descendant-or-self::o)"/>
        <xsl:variable name="self" select="exists($calls) and empty($loops)"/>
        <xsl:variable name="partner" select="if ($self) then () else eo:mutual(.)"/>
        <xsl:variable name="partners" select="($partner/@name, $partner/@local)/string()"/>
        <xsl:variable name="cross" select=".//o[ancestor::o[eo:abstract(.)][1] is current() and not(ancestor-or-self::o[eo:test-name(@name)]) and (some $n in $partners satisfies (@base = concat('ξ.ρ.', $n) or starts-with(@base, concat('ξ.ρ.', $n, '.'))))]"/>
        <xsl:if test="$self or exists($partner)">
          <defect>
            <xsl:variable name="line" select="eo:lineno(($calls, $cross)[1]/@line)"/>
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
            <xsl:value-of select="eo:escape((@local, @name)[1])"/>
            <xsl:text> cannot be turned into a loop, </xsl:text>
            <xsl:choose>
              <xsl:when test="not($self)">
                <xsl:text>it calls </xsl:text>
                <xsl:value-of select="eo:escape(($partner/@local, $partner/@name)[1])"/>
                <xsl:text>, which calls it back, and only a self-call becomes a loop</xsl:text>
              </xsl:when>
              <xsl:when test="o[@name='λ']">
                <xsl:text>the formation is an atom</xsl:text>
              </xsl:when>
              <xsl:when test=".//o[contains(@base, 'φ')]">
                <xsl:text>its body reads its own φ</xsl:text>
              </xsl:when>
              <xsl:when test="$calls[some $n in $names satisfies eo:is-self-dispatch(string(@base), $n)]">
                <xsl:text>the self-call is the receiver of a dispatch</xsl:text>
              </xsl:when>
              <xsl:when test="exists($direct)">
                <xsl:text>the self-call names the formation directly instead of through its receiver</xsl:text>
              </xsl:when>
              <xsl:when test="exists($elsewhere)">
                <xsl:text>the self-call is kept in an attribute other than φ</xsl:text>
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
