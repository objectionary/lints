<?xml version="1.0" encoding="UTF-8"?>
<!--
* SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
* SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" xmlns:xs="http://www.w3.org/2001/XMLSchema" id="void-name" version="2.0">
  <!--
  Since 0.63.3 the EO parser renames every void "x" of a formation to
  "v🌵x", together with every own read of it ("ξ.v🌵x") and every named
  binding that fills it ("@as" of "5:x"), so that the void can be filled
  from outside but not read (see "hide-voids" in "eo-parser"). Older
  parsers leave the name as written, thus a name without the marker is
  accepted too, so that `eo:lints` and `eo` may move independently.
  This function returns the name of a void as the programmer wrote it, e.g. `x` for `v🌵x`, or
  the "@base" of a read of it, e.g. `ξ.x` for `ξ.v🌵x`. Every consumer
  that shows a void name in a defect, or checks how it is spelled, must
  pass it through this function first. Any other name comes back
  untouched.
  -->
  <xsl:function name="eo:void-title" as="xs:string">
    <xsl:param name="name"/>
    <xsl:sequence select="replace(string($name), '(^|\.)v🌵', '$1')"/>
  </xsl:function>
</xsl:stylesheet>
