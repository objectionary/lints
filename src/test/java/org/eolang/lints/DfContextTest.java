/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

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
}
