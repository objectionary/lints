/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import fixtures.EoProgram;
import java.io.IOException;
import java.util.List;
import matchers.DefectMatcher;
import org.cactoos.io.InputOf;
import org.cactoos.list.ListOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link LtIncorrectUnlint}.
 * @since 0.0.38
 */
final class LtIncorrectUnlintTest {

    @Test
    void catchesIncorrectUnlints() throws IOException {
        MatcherAssert.assertThat(
            "unlint must point to existing lint",
            new LtIncorrectUnlint(List.of("hello")).defects(
                new EoProgram("org/eolang/lints/incorrect-unlints.eo").parse()
            ),
            Matchers.allOf(
                Matchers.<Defect>iterableWithSize(2),
                Matchers.everyItem(new DefectMatcher())
            )
        );
    }

    @Test
    void allowsCorrectUnlints() throws IOException {
        final String src = "+unlint ascii-only";
        MatcherAssert.assertThat(
            "Defects are not empty, but they shouldn't be",
            new LtIncorrectUnlint(List.of("ascii-only")).defects(
                new EoProgram(src, new InputOf(src)).parse()
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void providesClearMessage() throws IOException {
        MatcherAssert.assertThat(
            "Lint text doesn't contain clear message to the reader",
            new ListOf<>(
                new LtIncorrectUnlint(List.of("hello")).defects(
                    new EoProgram("org/eolang/lints/unlint-boom.eo").parse()
                )
            ).get(0).text(),
            Matchers.containsString("Suppressing \"boom\" does not make sense")
        );
    }

    @Test
    void understandsUnlintsWithLineNumber() throws IOException {
        MatcherAssert.assertThat(
            "Unlints with line number should be supported",
            new LtIncorrectUnlint(new ListOf<>("comment-not-capitalized")).defects(
                new EoProgram("org/eolang/lints/unlint-with-line-number.eo").parse()
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void catchesNonExistingUnlintWithLineNumber() throws IOException {
        MatcherAssert.assertThat(
            "Non existing unlint with line number should be caught",
            new LtIncorrectUnlint(new ListOf<>("a")).defects(
                new EoProgram("org/eolang/lints/unlint-non-existing-with-line.eo").parse()
            ),
            Matchers.hasSize(Matchers.greaterThan(0))
        );
    }
}
