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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests for {@link DfContext}.
 *
 * @since 0.0.40
 */
final class DfContextTest {

    @Test
    void printsContextAsXml() {
        MatcherAssert.assertThat(
            "XML does not match with expected",
            new DfContext(
                new Defect.Default(
                    "foo",
                    Severity.ERROR,
                    0,
                    "Boom!"
                ),
                String.join(
                    String.valueOf('\n'),
                    "<o base=\"man\">",
                    "<o as=\"name\" base=\"string\"/>",
                    "<o base=\"number\"/>",
                    "</o>"
                )
            ).context(),
            Matchers.equalTo(
                String.join(
                    String.valueOf('\n'),
                    "<o base=\"man\">",
                    "<o as=\"name\" base=\"string\"/>",
                    "<o base=\"number\"/>",
                    "</o>"
                )
            )
        );
    }

    @Test
    void preservesStandardEntities() {
        MatcherAssert.assertThat(
            "Standard entities were changed",
            new DfContext(
                new Defect.Default("foo", Severity.ERROR, 0, "Boom!"),
                "&quot;x&quot; &#39;y&#39; &apos;z&apos;"
            ).context(),
            Matchers.equalTo("&quot;x&quot; &#39;y&#39; &apos;z&apos;")
        );
    }

    @Test
    void preservesLiteralEntityText() {
        MatcherAssert.assertThat(
            "Literal entity text was decoded",
            new DfContext(
                new Defect.Default("foo", Severity.ERROR, 0, "Boom!"),
                "&amp;lt;o&amp;gt;"
            ).context(),
            Matchers.equalTo("&amp;lt;o&amp;gt;")
        );
    }

    @Test
    void preservesLineBreakEntityText() {
        MatcherAssert.assertThat(
            "Line break entity text was changed",
            new DfContext(
                new Defect.Default("foo", Severity.ERROR, 0, "Boom!"),
                "a&#10;b&#xA;c"
            ).context(),
            Matchers.equalTo("a&#10;b&#xA;c")
        );
    }

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

    @Test
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    void delegatesEveryOtherMethodToOrigin() {
        final Defect origin = new Defect.Default(
            "some-rule", Severity.WARNING, 42, "some text"
        );
        final DfContext defect = new DfContext(origin, "");
        MatcherAssert.assertThat(
            "rule() must be delegated to the origin defect",
            defect.rule(),
            Matchers.equalTo(origin.rule())
        );
        MatcherAssert.assertThat(
            "severity() must be delegated to the origin defect",
            defect.severity(),
            Matchers.equalTo(origin.severity())
        );
        MatcherAssert.assertThat(
            "line() must be delegated to the origin defect",
            defect.line(),
            Matchers.equalTo(origin.line())
        );
        MatcherAssert.assertThat(
            "text() must be delegated to the origin defect",
            defect.text(),
            Matchers.equalTo(origin.text())
        );
        MatcherAssert.assertThat(
            "version() must be delegated to the origin defect",
            defect.version(),
            Matchers.equalTo(origin.version())
        );
        MatcherAssert.assertThat(
            "experimental() must be delegated to the origin defect",
            defect.experimental(),
            Matchers.equalTo(origin.experimental())
        );
        MatcherAssert.assertThat(
            "toString() must be delegated to the origin defect",
            defect.toString(),
            Matchers.equalTo(origin.toString())
        );
    }
}
