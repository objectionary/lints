/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.log.Logger;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
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
                        "  # Bar",
                        "  [] > bar"
                    )
                ).parsed()
            ),
            Matchers.allOf(
                Matchers.iterableWithSize(1),
                Matchers.hasItem(
                    Matchers.hasToString(
                        Matchers.allOf(
                            Matchers.containsString("comment-without-dot WARNING"),
                            Matchers.containsString(":6")
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
                        "+unlint comment-without-dot:7",
                        "",
                        "# Foo",
                        "[] > foo",
                        "  # Bar",
                        "  [] > bar"
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
                        "  # Bar",
                        "  [] > bar"
                    )
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
                    String.join(
                        "\n",
                        "+home https://github.com/objectionary/eo",
                        "+package f",
                        "+version 0.0.0",
                        "+unlint comment-without-dot:7",
                        "",
                        "# No comments",
                        "[] > boom",
                        "  QQ.io.stdout > @",
                        "    \"we are booming!\""
                    )
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
                String.join(
                    "\n",
                    "# Foo with unused voids on the same line.",
                    "[x y z] > foo"
                )
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
}
