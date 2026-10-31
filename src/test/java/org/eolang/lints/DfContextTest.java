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
                "&lt;o base=&#34;man&#34;&gt;&#xA;&lt;o as=&#34;name&#34; base=&#34;string&#34;/&gt;&#xA;&lt;o base=&#34;number&#34;/&gt;&#xA;&lt;/o&gt;"
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
    void decodesStandardEntities() {
        MatcherAssert.assertThat(
            "Standard entities are not decoded",
            new DfContext(
                new Defect.Default("foo", Severity.ERROR, 0, "Boom!"),
                "&quot;x&quot; &#39;y&#39; &apos;z&apos;"
            ).context(),
            Matchers.equalTo("\"x\" 'y' 'z'")
        );
    }

    @Test
    void decodesDoubleEncodedEntities() {
        MatcherAssert.assertThat(
            "Double-encoded entities are not decoded",
            new DfContext(
                new Defect.Default("foo", Severity.ERROR, 0, "Boom!"),
                "&amp;lt;o&amp;gt;"
            ).context(),
            Matchers.equalTo("<o>")
        );
    }

    @Test
    void usesLfForLineBreaks() {
        MatcherAssert.assertThat(
            "Line breaks are not normalized to LF",
            new DfContext(
                new Defect.Default("foo", Severity.ERROR, 0, "Boom!"),
                "a&#10;b&#xA;c"
            ).context(),
            Matchers.equalTo(
                String.join(
                    String.valueOf('\n'),
                    "a",
                    "b",
                    "c"
                )
            )
        );
    }
}
