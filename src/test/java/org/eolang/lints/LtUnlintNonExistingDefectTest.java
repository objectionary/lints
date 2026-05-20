/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import java.io.IOException;
import java.util.stream.Collectors;
import org.cactoos.io.ResourceOf;
import org.cactoos.list.ListOf;
import org.eolang.parser.EoSyntax;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link LtUnlintNonExistingDefect}.
 * @since 0.0.40
 */
final class LtUnlintNonExistingDefectTest {

    @Test
    void reportsDefects() throws IOException {
        MatcherAssert.assertThat(
            "Defects are empty, but they should not",
            new LtUnlintNonExistingDefect(
                new ListOf<>(new LtAsciiOnly()),
                new ListOf<>()
            ).defects(
                new EoSyntax(
                    new ResourceOf("org/eolang/lints/unlint-ascii-only-no-defect.eo")
                ).parsed()
            ),
            Matchers.hasSize(Matchers.greaterThan(0))
        );
    }

    @Test
    void reportsDefectsForEachUnlintWithDifferentLines() throws IOException {
        MatcherAssert.assertThat(
            "Defects should be reported for each line with unlint, but it's not",
            new LtUnlintNonExistingDefect(
                new ListOf<>(new LtAsciiOnly()),
                new ListOf<>()
            ).defects(
                new EoSyntax(
                    new ResourceOf("org/eolang/lints/unlint-ascii-only-duplicate.eo")
                ).parsed()
            ).stream()
                .map(Defect::line)
                .collect(Collectors.toList()),
            Matchers.equalTo(
                new ListOf<>(1, 2)
            )
        );
    }

    @Test
    void allowsUnlintingOfExistingDefects() throws IOException {
        MatcherAssert.assertThat(
            "Defects are not empty, but they should",
            new LtUnlintNonExistingDefect(
                new ListOf<>(new LtAsciiOnly()),
                new ListOf<>()
            ).defects(
                new EoSyntax(
                    new ResourceOf("org/eolang/lints/unlint-ascii-only-chinese.eo")
                ).parsed()
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void allowsNoUnlints() throws IOException {
        MatcherAssert.assertThat(
            "Defects are not empty, but they should",
            new LtUnlintNonExistingDefect(
                new ListOf<>(new LtAsciiOnly()),
                new ListOf<>()
            ).defects(
                new EoSyntax(
                    new ResourceOf("org/eolang/lints/non-ascii-bar.eo")
                ).parsed()
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void allowsExistingUnlintWithLineNumber() throws IOException {
        MatcherAssert.assertThat(
            "An existing defect should be able to be unlinted with line number",
            new LtUnlintNonExistingDefect(
                new ListOf<>(new LtAsciiOnly()),
                new ListOf<>()
            ).defects(
                new EoSyntax(
                    new ResourceOf("org/eolang/lints/unlint-ascii-only-line.eo")
                ).parsed()
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void catchesNonExistingUnlintWithLineNumber() throws IOException {
        MatcherAssert.assertThat(
            "Non existing unlint with line number should be reported",
            new LtUnlintNonExistingDefect(
                new ListOf<>(new LtAsciiOnly()),
                new ListOf<>()
            ).defects(
                new EoSyntax(
                    new ResourceOf("org/eolang/lints/unlint-ascii-only-wrong-line.eo")
                ).parsed()
            ),
            Matchers.hasSize(Matchers.greaterThan(0))
        );
    }

    @Test
    void allowsUnlintForDefectsInTheLineRange() throws IOException {
        MatcherAssert.assertThat(
            "Defects are not empty, but they should",
            new LtUnlintNonExistingDefect(
                new ListOf<>(new LtAsciiOnly()),
                new ListOf<>()
            ).defects(
                new EoSyntax(
                    new ResourceOf("org/eolang/lints/unlint-ascii-only-range.eo")
                ).parsed()
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void catchesUnlintWithOutOfRangeLines() throws IOException {
        MatcherAssert.assertThat(
            "Defects are empty, but they should not",
            new LtUnlintNonExistingDefect(
                new ListOf<>(new LtAsciiOnly()),
                new ListOf<>()
            ).defects(
                new EoSyntax(
                    new ResourceOf("org/eolang/lints/unlint-ascii-only-out-range.eo")
                ).parsed()
            ),
            Matchers.iterableWithSize(1)
        );
    }

    @Test
    void catchesUnlintWithRangeForAbsentLint() throws IOException {
        MatcherAssert.assertThat(
            "Non-existing unlint with range should be reported, not crash with NPE",
            new LtUnlintNonExistingDefect(
                new ListOf<>(new LtAsciiOnly()),
                new ListOf<>()
            ).defects(
                new EoSyntax(
                    new ResourceOf("org/eolang/lints/unlint-ascii-only-absent.eo")
                ).parsed()
            ),
            Matchers.iterableWithSize(1)
        );
    }
}
