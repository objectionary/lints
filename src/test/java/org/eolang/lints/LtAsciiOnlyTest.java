/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import fixtures.EoProgram;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.cactoos.io.InputOf;
import org.cactoos.list.ListOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link LtAsciiOnly}.
 *
 * @since 0.0.59
 */
final class LtAsciiOnlyTest {

    @Test
    void catchesNonAsciiCharacterInComment() throws IOException {
        MatcherAssert.assertThat(
            "A non-ASCII character in a comment must be reported",
            new LtAsciiOnly().defects(
                LtAsciiOnlyTest.program("# Привет.")
            ),
            Matchers.hasSize(1)
        );
    }

    @Test
    void allowsAllAsciiComment() throws IOException {
        MatcherAssert.assertThat(
            "A comment with only ASCII characters must not be reported",
            new LtAsciiOnly().defects(
                LtAsciiOnlyTest.program("# Hello, world!")
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void allowsNewlineInsideMultilineComment() throws IOException {
        MatcherAssert.assertThat(
            "A newline character between lines of a comment is not an abusive character",
            new LtAsciiOnly().defects(
                LtAsciiOnlyTest.program("# First line.", "# Second line.")
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    void reportsCorrectRuleAndSeverity() throws IOException {
        final Defect defect = new ListOf<>(
            new LtAsciiOnly().defects(
                LtAsciiOnlyTest.program("# Привет.")
            )
        ).get(0);
        MatcherAssert.assertThat(
            "The defect must be reported under the ascii-only rule",
            defect.rule(),
            Matchers.equalTo("ascii-only")
        );
        MatcherAssert.assertThat(
            "A non-ASCII character in a comment is a warning, not an error",
            defect.severity(),
            Matchers.equalTo(Severity.WARNING)
        );
    }

    @Test
    void mentionsTheOffendingCharacterInMessage() throws IOException {
        MatcherAssert.assertThat(
            "The message must name the actual offending character",
            new ListOf<>(
                new LtAsciiOnly().defects(
                    LtAsciiOnlyTest.program("# Привет.")
                )
            ).get(0).text(),
            Matchers.containsString("П")
        );
    }

    @Test
    void mentionsSupplementaryUnicodeCharacterWholeInMessage() throws IOException {
        MatcherAssert.assertThat(
            "A supplementary character (surrogate pair) must be named whole, not as half a pair",
            new ListOf<>(
                new LtAsciiOnly().defects(
                    LtAsciiOnlyTest.program("# Hello 😀 world.")
                )
            ).get(0).text(),
            Matchers.containsString("😀")
        );
    }

    @Test
    void hasExpectedName() {
        MatcherAssert.assertThat(
            "The rule name must be ascii-only",
            new LtAsciiOnly().name(),
            Matchers.equalTo("ascii-only")
        );
    }

    private static XML program(final String... comment) {
        final List<String> src = new ArrayList<>(6);
        src.addAll(Arrays.asList(comment));
        src.add("");
        src.add("+spdx SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com");
        src.add("+spdx SPDX-License-Identifier: MIT");
        src.add("");
        src.add("[] > foo");
        final String txt = String.join(String.valueOf('\n'), src);
        return new EoProgram(txt, new InputOf(txt, StandardCharsets.UTF_8)).parse();
    }
}
