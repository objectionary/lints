/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import fixtures.EoProgram;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.cactoos.io.InputOf;
import org.cactoos.list.ListOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link LtSyntaxVersion}.
 *
 * @since 0.0.59
 */
final class LtSyntaxVersionTest {

    @Test
    void catchesSyntaxNewerThanParser() throws IOException {
        MatcherAssert.assertThat(
            "A +syntax newer than the parser must be reported as an error",
            new ListOf<>(
                new LtSyntaxVersion().defects(
                    LtSyntaxVersionTest.program("+syntax 999.0.0")
                )
            ),
            Matchers.hasSize(1)
        );
    }

    @Test
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    void reportsCorrectRuleAndSeverity() throws IOException {
        final Defect defect = new ListOf<>(
            new LtSyntaxVersion().defects(
                LtSyntaxVersionTest.program("+syntax 999.0.0")
            )
        ).get(0);
        MatcherAssert.assertThat(
            "The defect must be reported under the syntax-version rule as an error",
            defect.rule(),
            Matchers.equalTo("syntax-version")
        );
        MatcherAssert.assertThat(
            "A too-new required syntax must be an error, not a warning",
            defect.severity(),
            Matchers.equalTo(Severity.ERROR)
        );
        MatcherAssert.assertThat(
            "The message must mention the declared version",
            defect.text(),
            Matchers.containsString("999.0.0")
        );
    }

    @Test
    void allowsSyntaxOlderThanParser() throws IOException {
        MatcherAssert.assertThat(
            "A +syntax older than the parser must not be reported",
            new LtSyntaxVersion().defects(
                LtSyntaxVersionTest.program("+syntax 0.0.1")
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void ignoresProgramsWithoutSyntaxMeta() throws IOException {
        MatcherAssert.assertThat(
            "A program with no +syntax meta must not be reported",
            new LtSyntaxVersion().defects(
                LtSyntaxVersionTest.program()
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void ignoresMalformedSyntaxValue() throws IOException {
        MatcherAssert.assertThat(
            "A non-SemVer +syntax value must be silently ignored",
            new LtSyntaxVersion().defects(
                LtSyntaxVersionTest.program("+syntax abracadabra")
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void ignoresOverflowingSyntaxValue() throws IOException {
        MatcherAssert.assertThat(
            "An overflowing numeric +syntax value must be ignored, not crash the lint",
            new LtSyntaxVersion().defects(
                LtSyntaxVersionTest.program("+syntax 999999999999999999999999.2.3")
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void hasExpectedName() {
        MatcherAssert.assertThat(
            "The rule name must be syntax-version",
            new LtSyntaxVersion().name(),
            Matchers.equalTo("syntax-version")
        );
    }

    private static XML program(final String... metas) {
        final List<String> src = new ArrayList<>(3);
        src.addAll(Arrays.asList(metas));
        src.add("");
        src.add("# Foo.");
        src.add("[] > foo");
        final String txt = String.join(String.valueOf('\n'), src);
        return new EoProgram(txt, new InputOf(txt)).parse();
    }
}
