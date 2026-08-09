/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import fixtures.EoProgram;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link LtUnlint}.
 * @since 0.0.1
 */
final class LtUnlintTest {

    @Test
    void lintsOneFile() throws IOException {
        MatcherAssert.assertThat(
            "failed to return one error",
            new LtUnlint(new LtAlways()).defects(
                new EoProgram("org/eolang/lints/foo-without-dot.eo").parse()
            ),
            Matchers.hasSize(1)
        );
    }

    @Test
    void suppressesTheDefect() throws IOException {
        MatcherAssert.assertThat(
            "failed to return empty list",
            new LtUnlint(new LtAlways()).defects(
                new EoProgram("org/eolang/lints/unlint-always.eo").parse()
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void ignoresNonNumericLineSelector() throws IOException {
        MatcherAssert.assertThat(
            "Malformed unlint must not suppress a real defect",
            new LtUnlint(new LtAsciiOnly()).defects(
                new EoProgram(
                    "org/eolang/lints/unlint-ascii-only-non-numeric.eo"
                ).parse()
            ),
            Matchers.iterableWithSize(1)
        );
    }

    @Test
    void unlintsGrainy() throws IOException {
        MatcherAssert.assertThat(
            "Only one defect should be unlinted",
            new LtUnlint(new LtTestNotVerb()).defects(
                new EoProgram("org/eolang/lints/unlint-test-verb-only-grainy.eo").parse()
            ),
            Matchers.allOf(
                Matchers.iterableWithSize(1),
                Matchers.hasItem(
                    Matchers.hasToString(
                        Matchers.allOf(
                            Matchers.containsString("unit-test-is-not-verb WARNING"),
                            Matchers.containsString(":7")
                        )
                    )
                )
            )
        );
    }

    @Test
    void unlintsMultipleDefectsWithGranularUnlint() throws IOException {
        MatcherAssert.assertThat(
            "All defects should be unlinted",
            new LtUnlint(new LtAsciiOnly()).defects(
                new EoProgram("org/eolang/lints/unlint-ascii-only-multiple.eo").parse()
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void unlintsGlobally() throws IOException {
        MatcherAssert.assertThat(
            "All defects should be unlinted",
            new LtUnlint(
                new LtByXsl("comments/comment-without-dot")
            ).defects(new EoProgram("org/eolang/lints/unlint-comment-without-dot.eo").parse()),
            Matchers.emptyIterable()
        );
    }

    @Test
    void doesNotReportWhenUnlinted() throws IOException {
        MatcherAssert.assertThat(
            "Defect should not be reported when its unlinted",
            new LtUnlint(
                new LtByXsl("comments/comment-without-dot")
            ).defects(
                new EoProgram("org/eolang/lints/unlint-comment-without-dot-line.eo").parse()
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void supportsLineRanges() throws IOException {
        MatcherAssert.assertThat(
            "Defects are not empty, but they should",
            new LtUnlint(new LtTestNotVerb()).defects(
                new EoProgram("org/eolang/lints/unlint-test-verb-range.eo").parse()
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void catchesDefectsIfUnlintsOutOfRange() throws IOException {
        MatcherAssert.assertThat(
            "Resulted defects do not match with expected",
            new LtUnlint(new LtByXsl("aliases/alias-too-long")).defects(
                new EoProgram(
                    "org/eolang/lints/alias-too-long-out-of-range.eo"
                ).parse()
            ),
            Matchers.allOf(
                Matchers.iterableWithSize(1),
                Matchers.hasItem(
                    Matchers.hasToString(
                        Matchers.allOf(
                            Matchers.containsString("alias-too-long ERROR"),
                            Matchers.containsString(":4")
                        )
                    )
                )
            )
        );
    }

    @Test
    void catchesAllOutOfRangeDefects() throws IOException {
        MatcherAssert.assertThat(
            "Size of defects does not match with expected",
            new LtUnlint(new LtAsciiOnly()).defects(
                new EoProgram("org/eolang/lints/unlint-ascii-only-out-of-range.eo").parse()
            ),
            Matchers.iterableWithSize(1)
        );
    }

    @Test
    void keepsLineZeroDefectWithLineSpecificUnlint() throws IOException {
        MatcherAssert.assertThat(
            "A line-0 defect must not be silently dropped by a line-specific unlint",
            new LtUnlint(new LtByXsl("metas/mandatory-package")).defects(
                new EoProgram(
                    "org/eolang/lints/unlint-mandatory-package-line.eo"
                ).parse()
            ),
            Matchers.iterableWithSize(1)
        );
    }
}
