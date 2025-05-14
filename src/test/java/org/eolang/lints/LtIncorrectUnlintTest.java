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
                    "+unlint foo\n+unlint bar\n\n# Foo.\n[] > foo"
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
                        "+unlint boom\n\n# Foo.\n[] > foo"
                    ).parsed()
                )
            ).get(0).text(),
            Matchers.containsString("Suppressing \"boom\" does not make sense")
        );
    }

    @Test
    void understandsUnlintsWithLineNumber() throws IOException {
        MatcherAssert.assertThat(
            "Unlints with line number should be supported",
            new LtIncorrectUnlint(new ListOf<>("comment-not-capitalized"))
                .defects(
                    new EoSyntax(
                        String.join(
                            "\n",
                            "+unlint comment-not-capitalized:3",
                            "",
                            "# foo.",
                            "[] > foo"
                        )
                    ).parsed()
                ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void catchesNonExistingUnlintWithLineNumber() throws IOException {
        MatcherAssert.assertThat(
            "Non existing unlint with line number should be caught",
            new LtIncorrectUnlint(new ListOf<>("a")).defects(
                new EoSyntax(
                    String.join(
                        "\n",
                        "+unlint b:1",
                        "",
                        "# App.",
                        "[] > app"
                    )
                ).parsed()
            ),
            Matchers.hasSize(Matchers.greaterThan(0))
        );
    }
}
