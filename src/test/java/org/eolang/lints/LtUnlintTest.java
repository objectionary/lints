/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import java.io.IOException;
import org.eolang.parser.EoSyntax;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link LtUnlint}.
 *
 * @since 0.0.1
 */
final class LtUnlintTest {

    @Test
    void lintsOneFile() throws IOException {
        MatcherAssert.assertThat(
            "failed to return one error",
            new LtUnlint(new LtAlways()).defects(
                new EoSyntax("# first\n[] > foo\n").parsed()
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
                    "+unlint always\n\n# first\n[] > foo\n"
                ).parsed()
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void unlintsGrainy() throws IOException {
        MatcherAssert.assertThat(
            "Only one defect should be unlinted",
            new LtUnlint(
                new LtByXsl("comments/comment-without-dot")
            ).defects(
                new EoSyntax(
                    String.join(
                        "\n",
                        "+unlint comment-without-dot:4",
                        "",
                        "# Foo",
                        "[] > foo",
                        "",
                        "# Bar",
                        "[] > bar"
                    )
                ).parsed()
            ),
            Matchers.allOf(
                Matchers.iterableWithSize(1),
                Matchers.hasItem(
                    Matchers.hasToString(
                        Matchers.allOf(
                            Matchers.containsString("comment-without-dot WARNING"),
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
            new LtUnlint(
                new LtByXsl("comments/comment-without-dot")
            ).defects(
                new EoSyntax(
                    String.join(
                        "\n",
                        "+unlint comment-without-dot:5",
                        "+unlint comment-without-dot:8",
                        "",
                        "# Foo",
                        "[] > foo",
                        "",
                        "# Bar",
                        "[] > bar"
                    )
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
                    String.join(
                        "\n",
                        "+unlint comment-without-dot",
                        "",
                        "# Foo",
                        "[] > foo",
                        "",
                        "# Bar",
                        "[] > bar"
                    )
                ).parsed()
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void doesNotDuplicateDefects() throws IOException {
        MatcherAssert.assertThat(
            "Unlint should not duplicate defects",
            new LtUnlint(
                new LtByXsl("errors/noname-attribute")
            ).defects(
                new EoSyntax(
                    "app",
                    String.join(
                        "\n",
                        "+home https://github.com/objectionary/eo",
                        "+package f",
                        "+version 0.0.0",
                        "",
                        "# No comments.",
                        "[] > main",
                        "  QQ.io.stdout",
                        "    \"Hello world\""
                    )
                ).parsed()
            ),
            Matchers.iterableWithSize(1)
        );
    }

    @Test
    void doesNotReportWhenUnlinted() throws IOException {
        MatcherAssert.assertThat(
            "Defect should not be reported when its unlinted",
            new LtUnlint(
                new LtByXsl("errors/noname-attribute")
            ).defects(
                new EoSyntax(
                    "boom",
                    String.join(
                        "\n",
                        "+home https://github.com/objectionary/eo",
                        "+package f",
                        "+version 0.0.0",
                        "+unlint noname-attribute:8",
                        "",
                        "# No comments.",
                        "[] > boom",
                        "  QQ.io.stdout",
                        "    \"we are booming!\""
                    )
                ).parsed()
            ),
            Matchers.emptyIterable()
        );
    }
}
