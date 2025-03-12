/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import java.io.IOException;
import java.util.List;
import matchers.DefectMatcher;
import org.cactoos.list.ListOf;
import org.eolang.parser.EoSyntax;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link LtIncorrectUnlint}.
 *
 * @since 0.0.38
 */
final class LtIncorrectUnlintTest {
    @Test
    void catchesIncorrectUnlints() throws Exception {
        MatcherAssert.assertThat(
            "unlint must point to existing lint",
            new LtIncorrectUnlint(List.of("hello")).defects(
                new EoSyntax(
                    "+unlint foo\n+unlint bar"
                ).parsed()
            ),
            Matchers.allOf(
                Matchers.<Defect>iterableWithSize(2),
                Matchers.everyItem(new DefectMatcher())
            )
        );
    }

    @Test
    void allowsCorrectUnlints() throws Exception {
        MatcherAssert.assertThat(
            "Defects are not empty, but they shouldn't be",
            new LtIncorrectUnlint(List.of("ascii-only")).defects(
                new EoSyntax(
                    "+unlint ascii-only"
                ).parsed()
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
                    new EoSyntax(
                        "+unlint boom"
                    ).parsed()
                )
            ).get(0).text(),
            Matchers.containsString("Suppressing \"boom\" does not make sense")
        );
    }
}
