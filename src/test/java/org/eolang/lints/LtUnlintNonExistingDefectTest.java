/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import java.io.IOException;
import java.util.stream.Collectors;
import org.cactoos.list.ListOf;
import org.eolang.parser.EoSyntax;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link LtUnlintNonExistingDefect}.
 *
 * @since 0.0.40
 */
final class LtUnlintNonExistingDefectTest {

    @Test
    void reportsDefects() throws IOException {
        MatcherAssert.assertThat(
            "Defects are empty, but they should not",
            new LtUnlintNonExistingDefect(
                new ListOf<>(new LtAsciiOnly())
            ).defects(
                new EoSyntax(
                    String.join(
                        "\n",
                        "+unlint ascii-only",
                        "# first",
                        "# second",
                        "[] > bar"
                    )
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
                new ListOf<>(new LtAsciiOnly())
            ).defects(
                new EoSyntax(
                    String.join(
                        "\n",
                        "+unlint ascii-only",
                        "+unlint ascii-only",
                        "# first",
                        "# second",
                        "[] > bar"
                    )
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
                new ListOf<>(new LtAsciiOnly())
            ).defects(
                new EoSyntax(
                    String.join(
                        "\n",
                        "+unlint ascii-only",
                        "# 程式分析是我的熱愛",
                        "[] > bar"
                    )
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
                new ListOf<>(new LtAsciiOnly())
            ).defects(
                new EoSyntax(
                    String.join(
                        "\n",
                        "# тук-тук",
                        "[] > bar"
                    )
                ).parsed()
            ),
            Matchers.emptyIterable()
        );
    }

    /**
     * Ignores WPA unlint.
     * @throws IOException if something went wrong.
     * @todo #368:45min Configure `unlint-non-existing-defect` lint to ignore unlints from other
     *  scope (WPA/Single program). On WPA side of this lint
     *  ({@link LtUnlintNonExistingDefectWpaTest}) we should ignore single program lints. Also,
     *  don't forget to enable
     *  {@link LtUnlintNonExistingDefectWpaTest#ignoresSingleProgramUnlint()}.
     */
    @Disabled
    @Test
    void ignoresWpaUnlint() throws IOException {
        MatcherAssert.assertThat(
            "WPA unlints should be ignored",
            new LtUnlintNonExistingDefect(
                new ListOf<>(new LtAsciiOnly())
            ).defects(
                new EoSyntax(
                    String.join(
                        "\n",
                        "+unlint unit-test-missing",
                        "",
                        "# Buzz",
                        "[] > buzz"
                    )
                ).parsed()
            ),
            Matchers.emptyIterable()
        );
    }
}
