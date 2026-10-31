<?xml version="1.0" encoding="UTF-8"?>
<!--
* SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
* SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" xmlns:xs="http://www.w3.org/2001/XMLSchema" id="test-name" version="2.0">
  <!--
  The marker the EO parser puts in front of the `@name` of a unit test, or
  an empty string when the name is not a test at all. A truthy test is
  marked with `p🌵` and a throwing one with `n🌵` (see `Suffix.attribute`
  in `eo-parser`). Older parsers used `+` and `-` for the same purpose,
  which are not legal φ-calculus attribute names; both spellings are
  accepted here, so that `eo:lints` and `eo` may move independently.
  Markers differ in width, thus every consumer must strip them through
  `eo:test-title` instead of cutting one character off the name.
  -->
  <xsl:function name="eo:test-marker" as="xs:string">
    <xsl:param name="name"/>
    <xsl:sequence select="(('p🌵', 'n🌵', '+', '-')[starts-with($name, .)], '')[1]"/>
  </xsl:function>
  <!--
  TRUE if the given attribute name belongs to a unit test, no matter
  whether it is a truthy one or a throwing one.
  -->
  <xsl:function name="eo:test-name" as="xs:boolean">
    <xsl:param name="name"/>
    <xsl:sequence select="eo:test-marker($name) != ''"/>
  </xsl:function>
  <!--
  The name of a unit test with its marker removed, e.g. `can-do-it` for
  `p🌵can-do-it`. A name that is not a test comes back untouched.
  -->
  <xsl:function name="eo:test-title" as="xs:string">
    <xsl:param name="name"/>
    <xsl:sequence select="substring($name, string-length(eo:test-marker($name)) + 1)"/>
  </xsl:function>
  <!--
  TRUE if the given attribute name belongs to a truthy unit test. Such a
  test asserts that something works, thus it is a positive one, while a
  throwing test asserts that something fails, thus it is a negative one.
  -->
  <xsl:function name="eo:positive-test" as="xs:boolean">
    <xsl:param name="name"/>
    <xsl:sequence select="eo:test-marker($name) = ('p🌵', '+')"/>
  </xsl:function>
</xsl:stylesheet>
