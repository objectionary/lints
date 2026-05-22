/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.log.Logger;
import fixtures.EoProgram;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
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
    void unlintsGrainy() throws IOException {
        MatcherAssert.assertThat(
            "Only one defect should be unlinted",
            new LtUnlint(new LtAsciiOnly()).defects(
                new EoProgram("org/eolang/lints/unlint-ascii-only-grainy.eo").parse()
            ),
            Matchers.allOf(
                Matchers.iterableWithSize(1),
                Matchers.hasItem(
                    Matchers.hasToString(
                        Matchers.allOf(
                            Matchers.containsString("ascii-only WARNING"),
                            Matchers.containsString(":8")
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
    void doesNotDuplicateDefectsWhenMultipleDefectsOnTheSameLine() throws IOException {
        final Collection<Defect> defects = new LtUnlint(
            new LtByXsl("misc/unused-void-attr")
        ).defects(new EoProgram("org/eolang/lints/unused-voids.eo").parse());
        MatcherAssert.assertThat(
            Logger.format(
                "Found defects (%[list]s) contain duplicates, but they should not",
                defects
            ),
            new HashSet<>(defects).size() == defects.size(),
            Matchers.equalTo(true)
        );
    }

    @Test
    void supportsLineRanges() throws IOException {
        MatcherAssert.assertThat(
            "Defects are not empty, but they should",
            new LtUnlint(new LtByXsl("comments/comment-without-dot")).defects(
                new EoProgram("org/eolang/lints/unlint-comment-without-dot-range.eo").parse()
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void catchesDefectsIfUnlintsOutOfRange() throws IOException {
        MatcherAssert.assertThat(
            "Resulted defects do not match with expected",
            new LtUnlint(new LtByXsl("comments/comment-without-dot")).defects(
                new EoProgram(
                    "org/eolang/lints/unlint-comment-without-dot-out-of-range.eo"
                ).parse()
            ),
            Matchers.allOf(
                Matchers.iterableWithSize(1),
                Matchers.hasItem(
                    Matchers.hasToString(
                        Matchers.allOf(
                            Matchers.containsString("comment-without-dot WARNING"),
                            Matchers.containsString(":8")
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
            Matchers.iterableWithSize(2)
        );
    }
}
