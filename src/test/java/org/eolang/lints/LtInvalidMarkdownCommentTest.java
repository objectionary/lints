/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import java.io.IOException;
import java.util.Map;
import matchers.DefectMatcher;
import org.eolang.parser.EoSyntax;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link LtInvalidMarkdownComment}.
 *
 * @since 0.0.47
 */
final class LtInvalidMarkdownCommentTest {
    @Test
    void catchesMarkdownlintViolationsInComments() throws IOException {
        MatcherAssert.assertThat(
            "markdownlint found violation of its rules in comments",
            new LtInvalidMarkdownComment().defects(
                Map.of(
                    "test.eo",
                    new EoSyntax(
                        String.join(
                            "\n",
                            "# # Main object.",
                            "#",
                            "# This is main object of program.",
                            "#",
                            "# ### Subsection of main object.",
                            "#",
                            "# Sub information about main object.",
                            "[] > foo"
                        )
                    ).parsed()
                )
            ),
            Matchers.allOf(
                Matchers.<Defect>iterableWithSize(1),
                Matchers.<Defect>hasItem(
                    Matchers.hasToString(
                        Matchers.allOf(
                            Matchers.containsString("MD001"),
                            Matchers.containsString(":5")
                        )
                    )
                ),
                Matchers.<Defect>everyItem(new DefectMatcher())
            )
        );
    }

    @Test
    void allowsCorrectMarkdownlintComments() throws IOException {
        MatcherAssert.assertThat(
            "markdownlint found violation of its rules in comments",
            new LtInvalidMarkdownComment().defects(
                Map.of(
                    "test.eo",
                    new EoSyntax(
                        String.join(
                            "\n",
                            "# # Main object.",
                            "#",
                            "# This is main object of program.",
                            "#",
                            "# ## Subsection of main object.",
                            "#",
                            "# Sub information about main object.",
                            "[] > foo"
                        )
                    ).parsed()
                )
            ),
            Matchers.emptyIterable()
        );
    }
}
