<?xml version="1.0" encoding="UTF-8"?>
<!--
* SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
* SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" xmlns:xs="http://www.w3.org/2001/XMLSchema" id="written-meta" version="2.0">
  <!--
  The meta as it is written in the source, which is what metas are sorted
  by. The parser expands the tail of an `+alias` meta, turning
  `+alias org.eolang.io.stdout` into `stdout Φ.org.eolang.io.stdout`, so an
  alias is given by the path it was written with instead. Both the
  `unsorted-metas` lint and its fix must order metas by this text, or the
  fix leaves in place the very defects the lint reports.
  -->
  <xsl:function name="eo:written-meta" as="xs:string">
    <xsl:param name="meta"/>
    <xsl:sequence select="if ($meta/head = 'alias' and $meta/part[2]) then concat($meta/head, ' ', replace($meta/part[last()], '^Φ\.', '')) else concat($meta/head, ' ', $meta/tail)"/>
  </xsl:function>
</xsl:stylesheet>
