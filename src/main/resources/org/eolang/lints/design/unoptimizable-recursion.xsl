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
  Here and below up to "eo:cps" the functions repeat, nearly word by word,
  the ones of "_recursion.xsl" and "recursion-to-cps.xsl" in
  "eo-maven-plugin" (see objectionary/eo#5783), the two passes of the
  compiler that turn the recursion of a nested formation into a loop. They
  must stay in step with them, or this lint reports loops as recursion.
  -->
  <!--
  The place of the object among the arguments of its parent, counted from
  zero, or -1 when the object is no argument at all: every argument carries
  an @as, either "α0" or the name of the void it fills, and the receiver
  of a dispatch carries none.
  -->
  <xsl:function name="eo:arg" as="xs:integer">
    <xsl:param name="o" as="element()"/>
    <xsl:sequence select="if ($o/@as) then count($o/preceding-sibling::o[@as]) else -1"/>
  </xsl:function>
  <!--
  May the dispatch be the ".if" of a "bool"? Not when its receiver, "ξ.ρ.w"
  in "ξ.ρ.w.if" or the receiver of ".if", names a formation in this program
  that declares an "if" of its own, which does not have to return its
  argument as is.
  -->
  <xsl:function name="eo:choice" as="xs:boolean">
    <xsl:param name="o" as="element()"/>
    <xsl:variable name="receiver" select="if ($o/@base = '.if') then string($o/o[not(@as)][1]/@base) else replace($o/@base, '\.if$', '')"/>
    <xsl:variable name="steps" select="tokenize($receiver, '\.')"/>
    <xsl:sequence select="ends-with($o/@base, '.if') and not(matches($receiver, '^ξ(\.ρ)*\.[^.]+$') and exists($o/ancestor::o[not(@base)][count($steps) - 1]/o[not(@base) and @name = $steps[last()]]/o[@name = 'if']))"/>
  </xsl:function>
  <!--
  Is the object in a tail position of the expression rooted at $root? It is
  when every step from $root down to it is one of two: a branch of an ".if"
  that may belong to a "bool", its first or second argument, or the last
  element of the tuple a "seq" is applied to, which is the second argument
  of the "Φ.tuple" standing as the first argument of the "Φ.seq". Both
  answer exactly that element, so the value of the whole expression is the
  value of the object whenever the object is forced.
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
      <xsl:when test="eo:choice($parent) and eo:arg($o) = (0, 1)">
        <xsl:sequence select="eo:tail($parent, $root)"/>
      </xsl:when>
      <xsl:when test="$parent/@base = 'Φ.tuple' and eo:arg($parent) = 0 and $parent/parent::o/@base = 'Φ.seq' and eo:arg($o) = 1">
        <xsl:sequence select="eo:tail($parent/parent::o, $root)"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:sequence select="false()"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:function>
  <!-- How many "ρ" the reference climbs before it names anything: 2 for "ξ.ρ.ρ.x" -->
  <xsl:function name="eo:hops" as="xs:integer">
    <xsl:param name="base" as="xs:string"/>
    <xsl:sequence select="if (starts-with($base, 'ξ')) then string-length(replace($base, '^ξ((\.ρ)*)(\..*)?$', '$1')) idiv 2 else 0"/>
  </xsl:function>
  <!--
  The formation named at the head of the reference, "F" in "ξ.ρ.ρ.F" or in
  "ξ.ρ.ρ.F.x": the attribute "F" of the formation as many hops above the
  formation holding the reference as there are "ρ" in it.
  -->
  <xsl:function name="eo:formation" as="element()?">
    <xsl:param name="ref" as="element()"/>
    <xsl:variable name="base" select="string($ref/@base)"/>
    <xsl:variable name="hops" select="eo:hops($base)"/>
    <xsl:sequence select="if (matches($base, '^ξ(\.ρ)*\.[^.]')) then ($ref/ancestor::o[not(@base)][$hops + 1]/o[not(@base) and @name = tokenize($base, '\.')[$hops + 2]])[1] else ()"/>
  </xsl:function>
  <!--
  The attribute of the formation $f the reference names at its head, "A"
  in "ξ.A" from the scope of $f or in "ξ.ρ.A" from one formation below it.
  -->
  <xsl:function name="eo:attribute" as="element()?">
    <xsl:param name="ref" as="element()"/>
    <xsl:param name="f" as="element()"/>
    <xsl:variable name="base" select="string($ref/@base)"/>
    <xsl:variable name="hops" select="eo:hops($base)"/>
    <xsl:sequence select="if (matches($base, '^ξ(\.ρ)*\.[^.]') and $ref/ancestor::o[not(@base)][$hops + 1] is $f) then ($f/o[@name = tokenize($base, '\.')[$hops + 2]])[1] else ()"/>
  </xsl:function>
  <!-- Every reference to the formation $f, however it is spelled -->
  <xsl:function name="eo:refs" as="element()*">
    <xsl:param name="f" as="element()"/>
    <xsl:sequence select="$f/parent::o//o[@base][starts-with(@base, 'ξ') and matches(@base, concat('^ξ(\.ρ)*\.', $f/@name, '(\.|$)'))][eo:formation(.) is $f]"/>
  </xsl:function>
  <!--
  The attributes of $f the nodes refer to, directly or through other
  attributes, with $seen guarding against a cycle of attributes.
  -->
  <xsl:function name="eo:used" as="element()*">
    <xsl:param name="nodes" as="element()*"/>
    <xsl:param name="f" as="element()"/>
    <xsl:param name="seen" as="element()*"/>
    <xsl:variable name="attrs" select="(for $ref in $nodes/descendant-or-self::o[starts-with(@base, 'ξ')] return eo:attribute($ref, $f)) except ($seen, $nodes)"/>
    <xsl:sequence select="$attrs, if (exists($attrs)) then eo:used($attrs, $f, ($seen, $attrs)) else ()"/>
  </xsl:function>
  <!--
  The self-calls among $calls the nodes lead to: the ones among and below
  them, and the ones inside the attributes of $f they use.
  -->
  <xsl:function name="eo:reach" as="element()*">
    <xsl:param name="nodes" as="element()*"/>
    <xsl:param name="f" as="element()"/>
    <xsl:param name="calls" as="element()*"/>
    <xsl:sequence select="($nodes, eo:used($nodes, $f, ()))/descendant-or-self::o intersect $calls"/>
  </xsl:function>
  <!-- The attributes of $f that lead to a self-call among $calls -->
  <xsl:function name="eo:tainted" as="element()*">
    <xsl:param name="f" as="element()"/>
    <xsl:param name="calls" as="element()*"/>
    <xsl:sequence select="$f/o[not(@name='φ') and not(@base='∅')][exists(eo:reach(., $f, $calls))]"/>
  </xsl:function>
  <!--
  Why "recursion-to-cps.xsl" leaves the formation $f to recurse as it is,
  instead of rewriting it into continuation-passing style, where every
  self-call is a tail call and the recursion runs as a loop; or nothing,
  when it rewrites it. These are the shapes its header lists as skipped,
  except for a "λ" and a read of "φ", which the main template tells
  before it gets here.
  -->
  <xsl:function name="eo:cps-obstacle" as="xs:string?">
    <xsl:param name="f" as="element()"/>
    <xsl:variable name="phi" select="$f/o[@name='φ' and @base]"/>
    <xsl:variable name="calls" select="$f//o[@base = concat('ξ.ρ.', $f/@name)]"/>
    <xsl:variable name="tainted" select="eo:tainted($f, $calls)"/>
    <xsl:variable name="refs" select="eo:refs($f)"/>
    <xsl:choose>
      <xsl:when test="empty($f/parent::o) or empty($phi)">
        <xsl:sequence select="'no self-call sits in a tail position'"/>
      </xsl:when>
      <xsl:when test="empty(eo:reach($phi, $f, $calls)[not(eo:tail(., $phi))]) or (some $a in $tainted satisfies empty($a intersect eo:used($phi, $f, ())))">
        <xsl:sequence select="'the self-call is kept in an attribute that φ does not lead to'"/>
      </xsl:when>
      <xsl:when test="exists($phi/descendant-or-self::o[ends-with(@base, '.if') and not(eo:choice(.))][exists(eo:reach(., $f, $calls))])">
        <xsl:sequence select="'the self-call sits under a choice by an object that may not be a boolean'"/>
      </xsl:when>
      <xsl:when test="exists($phi//o[@name][not(ancestor::o[not(@base)][. &gt;&gt; $phi])])">
        <xsl:sequence select="'its φ binds a named attribute'"/>
      </xsl:when>
      <xsl:when test="some $call in $calls satisfies exists(eo:reach($call/o, $f, $calls))">
        <xsl:sequence select="'a self-call sits inside the arguments of another self-call'"/>
      </xsl:when>
      <xsl:when test="some $call in $calls satisfies not(eo:formation($call) is $f)">
        <xsl:sequence select="'a self-call sits inside a nested formation'"/>
      </xsl:when>
      <xsl:when test="some $ref in $refs satisfies (exists($ref/ancestor::o[. is $f]) and empty($ref intersect $calls))">
        <xsl:sequence select="'a self-call sits inside a nested formation'"/>
      </xsl:when>
      <xsl:when test="some $ref in $refs satisfies not(matches($ref/@base, '^ξ(\.ρ)*\.[^.]+$'))">
        <xsl:sequence select="'an attribute of the formation is taken without applying it'"/>
      </xsl:when>
      <xsl:when test="exists($tainted) and exists($refs[not(@as)]/parent::o[starts-with(@base, '.')])">
        <xsl:sequence select="'it is the receiver of a dispatch while its self-call is kept in an attribute'"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:sequence select="()"/>
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
  only when the compiler recognises the shape: either a "^" self-call in a
  tail position of φ, which "recursion-to-loop.xsl" handles, or a shape that
  "recursion-to-cps.xsl" rewrites into tail calls first. When it does not, the program
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
        <xsl:variable name="recursive" select="exists($calls) and empty($loops)"/>
        <!--
        Only the "^" spelling is rewritten into continuation-passing style,
        so a formation with a bare or "$" self-call keeps recursing.
        -->
        <xsl:variable name="rewritable" select="$recursive and empty(o[@name='λ']) and empty(.//o[contains(@base, 'φ')]) and (every $c in $calls satisfies $c/@base = concat('ξ.ρ.', $name))"/>
        <xsl:variable name="obstacle" select="if ($rewritable) then eo:cps-obstacle(.) else ()" as="xs:string?"/>
        <xsl:variable name="self" select="$recursive and not($rewritable and empty($obstacle))"/>
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
              <xsl:when test="exists($obstacle)">
                <xsl:value-of select="$obstacle"/>
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
