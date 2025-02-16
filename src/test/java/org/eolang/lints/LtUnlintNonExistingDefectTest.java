/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2025 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.eolang.lints;

import java.io.IOException;
import java.util.stream.Collectors;
import org.cactoos.list.ListOf;
import org.eolang.parser.EoSyntax;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link LtUnlintNonExistingDefect}.
 *
 * @since 0.0.0
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
}
