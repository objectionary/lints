<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" id="redundant-object" version="2.0">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="//o[@name and @name != '@' and @base and @base != '∅']">
        <xsl:variable name="usage" select="concat('^\$(?:\.\^)*\.', @name, '(?:\.\w+)?$')"/>
        <!--
          @todo #376:45min Define usage of iteration objects properly.
           Currently, in the `$looped` and `$looped-auto` we are looking for objects with bases
           equal to either `.eachi` or `.each`. It can be more iteration objects in EO, besides
           that, custom iteration objects can be introduced by the programmers. Also, since EO is
           dynamically-typed language, and we don't have predefined control flow in the language,
           we can't denote which object is an iteration one. Probably, the solution is to allow
           redundant object to be composed, and used by the different object. Like in this example:
           ```eo
           # Foo.
           [] > foo
             52 > created  # not redundant
             x.created > ...
             ...
             42 > f # redundant
             f.plus 4 > ...
           ```
        -->
        <xsl:variable name="looped" select="boolean(//o[@base = '.eachi' or @base = '.each']/o[matches(@base, $usage)])"/>
        <xsl:variable name="looped-auto" select="boolean(//o[@name=substring(//o[@base = '.eachi' or @base = '.each']/o[starts-with(@base, '$.a🌵')]/@base, 3)]/o[matches(@base, $usage)])"/>
        <xsl:if test="count(//o[matches(@base, $usage)])&lt;=1 and not($looped) and not($looped-auto)">
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
            <xsl:value-of select="eo:escape(@name)"/>
            <xsl:text> is redundant, consider inline it instead</xsl:text>
          </xsl:element>
        </xsl:if>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
