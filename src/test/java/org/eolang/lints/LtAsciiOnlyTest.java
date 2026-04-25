/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import java.io.IOException;
import matchers.DefectMatcher;
import org.cactoos.list.ListOf;
import org.eolang.parser.EoSyntax;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link LtAsciiOnly}.
 * @since 0.0.1
 */
final class LtAsciiOnlyTest {

    @Test
    void catchesSomeNonAsciiInComment() throws IOException {
        MatcherAssert.assertThat(
            "non-ascii comment is not welcome",
            new LtAsciiOnly().defects(
                new EoSyntax(
                    String.join(
                        System.lineSeparator(),
                        "# привет",
                        "# как дела?",
                        "[] > foo",
                        ""
                    )
                ).parsed()
            ),
            Matchers.allOf(
                Matchers.<Defect>iterableWithSize(Matchers.greaterThan(0)),
                Matchers.<Defect>everyItem(new DefectMatcher())
            )
        );
    }

    @Test
    void catchesNonAsciiInComment() throws IOException {
        MatcherAssert.assertThat(
            "non-ascii comment error should contain abusive character",
            new ListOf<>(
                new LtAsciiOnly().defects(
                    new EoSyntax(
                        String.join(
                            System.lineSeparator(),
                            "# привет",
                            "# как дела?",
                            "[] > foo",
                            ""
                        )
                    ).parsed()
                )
            ).get(0).text(),
            Matchers.containsString("Only ASCII characters are allowed in comments")
        );
    }

    @Test
    void explainsMotive() throws Exception {
        MatcherAssert.assertThat(
            "The motive doesn't contain expected string",
            new LtAsciiOnly().motive().contains("# ASCII-Only Characters in Comments"),
            new IsEqual<>(true)
        );
    }

    @Test
    void setsRuleCorrectly() throws Exception {
        MatcherAssert.assertThat(
            "The rule name is set right",
            new LtAsciiOnly().defects(
                new EoSyntax(
                    String.join(
                        System.lineSeparator(),
                        "# тук тук",
                        "[] > foo",
                        ""
                    )
                ).parsed()
            ).iterator().next().rule(),
            Matchers.equalTo("ascii-only")
        );
    }

    @Test
    void complainsAsWarning() throws IOException {
        MatcherAssert.assertThat(
            "The lint should complain as warning",
            new LtAsciiOnly().defects(
                new EoSyntax(
                    String.join(
                        System.lineSeparator(),
                        "# 计算机编程是我的生活!",
                        "[] > a"
                    )
                ).parsed()
            ).iterator().next().severity(),
            Matchers.equalTo(Severity.WARNING)
        );
    }
}
