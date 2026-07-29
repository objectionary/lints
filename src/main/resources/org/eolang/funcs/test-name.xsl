<?xml version="1.0"?>
<!--
* SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
* SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" xmlns:xs="http://www.w3.org/2001/XMLSchema" id="test-name" version="2.0">
  <!--
  TRUE if the given attribute name belongs to a unit test. The EO parser
  marks a truthy test attribute by prefixing its `@name` with a plus, and a
  throwing test attribute by prefixing it with a minus (see
  `Suffix.attribute` in `eo-parser`). Both kinds are tests.
  -->
  <xsl:function name="eo:test-name" as="xs:boolean">
    <xsl:param name="name"/>
    <xsl:sequence select="starts-with($name, '+') or starts-with($name, '-')"/>
  </xsl:function>
</xsl:stylesheet>
