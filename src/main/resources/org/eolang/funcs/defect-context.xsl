<?xml version="1.0" encoding="UTF-8"?>
<!--
* SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
* SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" xmlns:xs="http://www.w3.org/2001/XMLSchema" id="defect-context" version="2.0">
  <!--
  The first 300 characters of the serialized node, to show next to a
  defect that has no line. Serializing the whole node would cost as
  much as the node is big, and a defect on the top object of a large
  program would serialize the entire program to keep 300 characters of
  it. Every node contributes at least one character to the output, so
  the first 300 characters never reach past the first 300 nodes in
  document order. We copy only those nodes, keeping their structure,
  and serialize the copy, which gives exactly the same prefix. A node
  that small anyway is serialized as is.
  -->
  <xsl:function name="eo:defect-context" as="xs:string">
    <xsl:param name="context"/>
    <xsl:choose>
      <xsl:when test="empty($context/descendant-or-self::node()[301])">
        <xsl:value-of select="substring(serialize($context), 1, 300)"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:variable name="pruned">
          <xsl:apply-templates select="$context" mode="eo:defect-context">
            <xsl:with-param name="last" select="$context/descendant-or-self::node()[300]" tunnel="yes"/>
          </xsl:apply-templates>
        </xsl:variable>
        <xsl:value-of select="substring(serialize($pruned), 1, 300)"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:function>
  <!--
  The kept nodes are a prefix in document order, so a node is kept
  when it does not follow the last of them.
  -->
  <xsl:template match="*" mode="eo:defect-context">
    <xsl:param name="last" tunnel="yes"/>
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates select="node()[not(. &gt;&gt; $last)]" mode="eo:defect-context"/>
    </xsl:copy>
  </xsl:template>
  <xsl:template match="node()" mode="eo:defect-context" priority="-1">
    <xsl:copy-of select="."/>
  </xsl:template>
</xsl:stylesheet>
