/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import fixtures.EoProgram;
import java.io.IOException;
import matchers.DefectMatcher;
import org.cactoos.list.ListOf;
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
                new EoProgram("org/eolang/lints/non-ascii-cyrillic.eo").parse()
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
                    new EoProgram("org/eolang/lints/non-ascii-cyrillic.eo").parse()
                )
            ).get(0).text(),
            Matchers.containsString("Only ASCII characters are allowed in comments")
        );
    }

    @Test
    void explainsMotive() throws IOException {
        MatcherAssert.assertThat(
            "The motive doesn't contain expected string",
            new LtAsciiOnly().motive().contains("# ASCII-Only Characters in Comments"),
            new IsEqual<>(true)
        );
    }

    @Test
    void setsRuleCorrectly() throws IOException {
        MatcherAssert.assertThat(
            "The rule name is set right",
            new LtAsciiOnly().defects(
                new EoProgram("org/eolang/lints/non-ascii-tuk-tuk.eo").parse()
            ).iterator().next().rule(),
            Matchers.equalTo("ascii-only")
        );
    }

    @Test
    void complainsAsWarning() throws IOException {
        MatcherAssert.assertThat(
            "The lint should complain as warning",
            new LtAsciiOnly().defects(
                new EoProgram("org/eolang/lints/non-ascii-chinese.eo").parse()
            ).iterator().next().severity(),
            Matchers.equalTo(Severity.WARNING)
        );
    }
}
