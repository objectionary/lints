/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
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
                new ListOf<>(new LtUnlintNonExistingDefectTest.Boom()),
                new ListOf<>()
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
                ),
                new ListOf<>()
            ).defects(
                new EoProgram("org/eolang/lints/unlint-ascii-only-no-defect.eo").parse()
            ),
            Matchers.hasSize(Matchers.greaterThan(0))
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
