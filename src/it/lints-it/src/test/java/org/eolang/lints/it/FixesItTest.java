/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints.it;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.io.IOException;
import org.eolang.lints.Source;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for fix pipeline via {@link Source}.
 *
 * @since 0.2.1
 */
final class FixesItTest {

    @Test
    void fixesUnsortedMetas() throws IOException {
        final XML xmir = new XMLDocument(
            String.join(
                "",
                "<object>",
                "<metas>",
                "<meta line='1'><head>version</head><tail>0.0.0</tail></meta>",
                "<meta line='2'><head>spdx</head><tail>SPDX-License-Identifier: MIT</tail></meta>",
                "</metas>",
                "</object>"
            )
        );
        final Source source = new Source(xmir);
        MatcherAssert.assertThat(
            "unsorted-metas defect must be detected before fix",
            source.defects().stream().anyMatch(d -> d.rule().startsWith("unsorted-metas")),
            Matchers.is(true)
        );
        final XML fixed = source.fix();
        MatcherAssert.assertThat(
            "no unsorted-metas defect must remain after fix is applied",
            new Source(fixed).defects().stream().noneMatch(d -> d.rule().startsWith("unsorted-metas")),
            Matchers.is(true)
        );
    }
}
