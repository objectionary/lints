<?xml version="1.0"?>
<!--
* SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
* SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" xmlns:xs="http://www.w3.org/2001/XMLSchema" id="home-object" version="2.0">
  <!--
  Names of the root objects that live directly under "eo-runtime/src/main/eo"
  in the "objectionary/eo" repository. These objects belong to "org.eolang"
  by design: the transpiler emits "package org.eolang" for them itself, so
  neither "+package" (mandatory-package) nor "+package org.eolang"
  (prohibited-package) should be flagged on them. Keep this list in sync
  with the home repository; both lints share it from here so it never goes
  stale in two places at once. The "+home" tail is compared with its trailing
  slashes cut off, because "https://github.com/objectionary/eo/" names the
  same repository as the URL without the slash, and an exact match would let
  a slash turn a runtime object into a defect of both lints (#1379).
  -->
  <xsl:variable name="eo:home-objects" as="xs:string*" select="('bool', 'buffer', 'bytes', 'chunk', 'clock', 'console', 'dataized', 'directory', 'e', 'eol', 'false', 'file', 'getenv', 'i16', 'i32', 'i64', 'i8', 'input', 'malloc', 'map', 'mktemp', 'nan', 'ninf', 'nop', 'number', 'os', 'output', 'path', 'pi', 'pinf', 'posix', 'range', 'recovered', 'seq', 'set', 'socket', 'stderr', 'stdin', 'stdout', 'string', 'switch', 'true', 'tuple', 'u16', 'u32', 'u64', 'u8', 'uri', 'while', 'win32')"/>
  <xsl:variable name="eo:home-repo" as="xs:string" select="'https://github.com/objectionary/eo'"/>
  <xsl:function name="eo:home-object" as="xs:boolean">
    <xsl:param name="name" as="xs:string?"/>
    <xsl:param name="metas" as="element()*"/>
    <xsl:sequence select="$name = $eo:home-objects and $metas[head = 'home' and replace(tail, '/+$', '') = $eo:home-repo]"/>
  </xsl:function>
</xsl:stylesheet>
