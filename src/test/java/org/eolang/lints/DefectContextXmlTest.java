/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Diagnostic contexts extracted from real stylesheet output.
 *
 * @since 0.0.0
 */
final class DefectContextXmlTest {

    @ParameterizedTest
    @ValueSource(strings = {"a&amp;b", "a&amp;lt;b", "a&quot;b", "a&#10;b"})
    void preservesNamesThroughStylesheetAndScoping(final String name) {
        final XML input = new XMLDocument(
            String.format("<object><o name=\"%s\"/></object>", name)
        );
        final Defect defect = new LtByXsl("names/non-kebab-name")
            .defects(input).iterator().next();
        MatcherAssert.assertThat(
            "Diagnostic contexts changed the name of the original object",
            Arrays.asList(
                defect, new ScopedDefects(Collections.singleton(defect), "test").iterator().next()
            ).stream()
                .map(item -> new XMLDocument(item.context()).xpath("/o/@name").get(0))
                .collect(Collectors.toList()),
            Matchers.everyItem(Matchers.equalTo(input.xpath("/object/o/@name").get(0)))
        );
    }
}
