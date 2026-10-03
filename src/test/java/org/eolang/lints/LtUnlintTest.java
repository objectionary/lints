/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.io.IOException;
import java.util.Collection;
import org.cactoos.list.ListOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link LtUnlint}.
 *
 * @since 0.0.1
 */
final class LtUnlintTest {

    @Test
    void keepsFindingsWhenSelectorDoesNotFitAnInteger() throws IOException {
        final Collection<Defect> defects = new LtUnlint(
            new LtUnlintTest.FakeLint()
        ).defects(
            new XMLDocument(
                "<object><metas><meta line='1'><head>unlint</head>"
                    + "<tail>fake:2147483648</tail></meta></metas></object>"
            )
        );
        MatcherAssert.assertThat(
            "An invalid selector must not suppress findings or crash the lint",
            defects,
            Matchers.hasSize(1)
        );
    }

    /**
     * Lint that always reports a fixed finding.
     *
     * @since 0.0.1
     */
    private static final class FakeLint implements Lint {

        @Override
        public String name() {
            return "fake";
        }

        @Override
        public Collection<Defect> defects(final XML xmir) {
            return new ListOf<>(
                new Defect.Default("fake", Severity.WARNING, 1, "fake finding")
            );
        }

        @Override
        public String motive() {
            return "";
        }

        @Override
        public Fix fix() {
            return new FxEmpty();
        }
    }
}
