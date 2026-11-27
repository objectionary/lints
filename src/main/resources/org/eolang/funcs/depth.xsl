<?xml version="1.0"?>
<!--
* SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
* SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" xmlns:xs="http://www.w3.org/2001/XMLSchema" id="depth" version="2.0">
  <!--
  Depth of the object in its nesting chain, counting only the ancestors that
  represent real nesting: the wrapper objects ("Φ.tuple", "Φ.bytes",
  "Φ.number", "Φ.string") do not count. Both "too-deep-object" and
  "broad-scope" measure nesting with it, so that "broad-scope" never
  suggests a move that "too-deep-object" would then report (#1844).
  -->
  <xsl:function name="eo:depth" as="xs:integer">
    <xsl:param name="o" as="element()"/>
    <xsl:sequence select="count($o/ancestor::o[not(@base='Φ.tuple') and not(@base='Φ.bytes') and not(@base='Φ.number') and not(@base='Φ.string')])"/>
  </xsl:function>
  <!--
  TRUE when the node is a part of a literal rather than an object of the
  source: the data inside a "Φ.bytes", or the "Φ.bytes" that holds the data
  of a "Φ.number" or a "Φ.string". Since the wrappers do not count, such a
  node is as deep as its literal, so the literal is reported instead of it.
  -->
  <xsl:function name="eo:literal-part" as="xs:boolean">
    <xsl:param name="o" as="element()"/>
    <xsl:sequence select="(empty($o/@base) and $o/parent::o/@base = 'Φ.bytes') or ($o/@base = 'Φ.bytes' and $o/parent::o/@base = ('Φ.number', 'Φ.string'))"/>
  </xsl:function>
  <!--
  The deepest level, measured by "eo:depth", that the object itself or
  any of its descendants reaches, skipping the parts of literals.
  -->
  <xsl:function name="eo:deepest" as="xs:integer">
    <xsl:param name="o" as="element()"/>
    <xsl:sequence select="max(for $d in $o/descendant-or-self::o[not(eo:literal-part(.))] return eo:depth($d))"/>
  </xsl:function>
</xsl:stylesheet>
