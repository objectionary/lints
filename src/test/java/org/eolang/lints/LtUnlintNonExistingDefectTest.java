/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import fixtures.EoProgram;
import java.io.IOException;
import java.util.Collection;
import org.cactoos.list.ListOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link LtUnlintNonExistingDefect}.
 *
 * @since 0.0.40
 */
final class LtUnlintNonExistingDefectTest {

    @Test
    void doesNotRunLintsWithoutUnlints() throws IOException {
        MatcherAssert.assertThat(
            "Lints should not be executed when there are no +unlint metas",
            new LtUnlintNonExistingDefect(
                new ListOf<>(new LtUnlintNonExistingDefectTest.Boom())
            ).defects(
                new EoProgram("org/eolang/lints/non-ascii-bar.eo").parse()
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void doesNotInvokeUnreferencedLint() throws IOException {
        MatcherAssert.assertThat(
            "Only the referenced lint should be executed",
            new LtUnlintNonExistingDefect(
                new ListOf<>(
                    new LtAsciiOnly(),
                    new LtUnlintNonExistingDefectTest.Boom()
                )
            ).defects(
                new EoProgram("org/eolang/lints/unlint-ascii-only-no-defect.eo").parse()
            ),
            Matchers.hasSize(Matchers.greaterThan(0))
        );
    }

    @Test
    void quotesUnlintValuesContainingBothQuoteCharacters() throws IOException {
        MatcherAssert.assertThat(
            "A malformed suppression should produce a defect instead of breaking XPath",
            new LtUnlintNonExistingDefect(new ListOf<>()).defects(
                new XMLDocument(
                    "<object><metas><meta line='1'><head>unlint</head>"
                        + "<tail>missing\"and'bad</tail></meta></metas></object>"
                )
            ),
            Matchers.hasSize(1)
        );
    }

    @Test
    void reportsMissingDefectWhenUnlintMetaHasNoLine() throws IOException {
        final Collection<Defect> defects = new LtUnlintNonExistingDefect(
            new ListOf<>()
        ).defects(
            new XMLDocument(
                "<object><metas><meta><head>unlint</head><tail>ascii-only:999</tail>"
                    + "</meta></metas></object>"
            )
        );
        MatcherAssert.assertThat(
            "A suppression without a line should still report its missing defect",
            defects,
            Matchers.hasSize(1)
        );
        MatcherAssert.assertThat(
            "Missing source line falls back to line zero",
            defects.iterator().next().line(),
            Matchers.equalTo(0)
        );
    }

    /**
     * Fake lint that explodes when invoked.
     *
     * @since 0.0.40
     */
    private static final class Boom implements Lint {

        @Override
        public String name() {
            return "boom";
        }

        @Override
        public Collection<Defect> defects(final XML xmir) {
            throw new IllegalStateException("this lint must not be executed");
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
