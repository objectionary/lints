/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.log.Logger;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import org.cactoos.io.ResourceOf;
import org.eolang.parser.EoSyntax;
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
                new EoSyntax(
                    new ResourceOf("org/eolang/lints/first-foo.eo")
                ).parsed()
            ),
            Matchers.hasSize(1)
        );
    }

    @Test
    void suppressesTheDefect() throws IOException {
        MatcherAssert.assertThat(
            "failed to return empty list",
            new LtUnlint(new LtAlways()).defects(
                new EoSyntax(
                    new ResourceOf("org/eolang/lints/unlint-always.eo")
                ).parsed()
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void unlintsGrainy() throws IOException {
        MatcherAssert.assertThat(
            "Only one defect should be unlinted",
            new LtUnlint(new LtAsciiOnly()).defects(
                new EoSyntax(
                    new ResourceOf("org/eolang/lints/unlint-ascii-only-grainy.eo")
                ).parsed()
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
                new EoSyntax(
                    new ResourceOf("org/eolang/lints/unlint-ascii-only-multiple.eo")
                ).parsed()
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
            ).defects(
                new EoSyntax(
                    new ResourceOf("org/eolang/lints/unlint-comment-without-dot.eo")
                ).parsed()
            ),
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
                new EoSyntax(
                    new ResourceOf("org/eolang/lints/unlint-comment-without-dot-line.eo")
                ).parsed()
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void doesNotDuplicateDefectsWhenMultipleDefectsOnTheSameLine() throws IOException {
        final Collection<Defect> defects = new LtUnlint(
            new LtByXsl("misc/unused-void-attr")
        ).defects(
            new EoSyntax(
                new ResourceOf("org/eolang/lints/unused-voids.eo")
            ).parsed()
        );
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
                new EoSyntax(
                    new ResourceOf("org/eolang/lints/unlint-comment-without-dot-range.eo")
                ).parsed()
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void catchesDefectsIfUnlintsOutOfRange() throws IOException {
        MatcherAssert.assertThat(
            "Resulted defects do not match with expected",
            new LtUnlint(new LtByXsl("comments/comment-without-dot")).defects(
                new EoSyntax(
                    new ResourceOf("org/eolang/lints/unlint-comment-without-dot-out-of-range.eo")
                ).parsed()
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
                new EoSyntax(
                    new ResourceOf("org/eolang/lints/unlint-ascii-only-out-of-range.eo")
                ).parsed()
            ),
            Matchers.iterableWithSize(2)
        );
    }
}
