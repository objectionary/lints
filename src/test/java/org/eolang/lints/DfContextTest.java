/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
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
                    "app.main",
                    0,
                    "Boom!"
                ),
                "&lt;o base=&#34;man&#34;&gt;&#xA;&lt;o as=&#34;name&#34; base=&#34;string&#34;/&gt;&#xA;&lt;o base=&#34;number&#34;/&gt;&#xA;&lt;/o&gt;"
            ).context(),
            Matchers.equalTo(
                "<o base=\"man\">\n<o as=\"name\" base=\"string\"/>\n<o base=\"number\"/>\n</o>"
            )
        );
    }
}
