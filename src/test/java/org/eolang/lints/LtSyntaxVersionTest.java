/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.github.lombrozo.xnav.Xnav;
import fixtures.EoProgram;
import java.io.IOException;
import matchers.DefectMatcher;
import org.cactoos.io.InputOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link LtSyntaxVersion}.
 *
 * @since 0.2.11
 */
final class LtSyntaxVersionTest {

    @Test
    void allowsSyntaxVersionOlderThanParser() throws IOException {
        final String src = LtSyntaxVersionTest.program("+syntax 0.0.1");
        MatcherAssert.assertThat(
            "declaring an old +syntax version must not cause defects",
            new LtSyntaxVersion().defects(new EoProgram(src, new InputOf(src)).parse()),
            Matchers.emptyIterable()
        );
    }

    @Test
    void allowsSyntaxVersionEqualToParser() throws IOException {
        final String src = LtSyntaxVersionTest.program(
            String.format("+syntax %s", LtSyntaxVersionTest.actualVersion())
        );
        MatcherAssert.assertThat(
            "declaring the exact parser version must not cause defects",
            new LtSyntaxVersion().defects(new EoProgram(src, new InputOf(src)).parse()),
            Matchers.emptyIterable()
        );
    }

    @Test
    void catchesSyntaxVersionNewerThanParser() throws IOException {
        final String src = LtSyntaxVersionTest.program("+syntax 999.0.0");
        MatcherAssert.assertThat(
            "declaring a +syntax version newer than the parser must be caught",
            new LtSyntaxVersion().defects(new EoProgram(src, new InputOf(src)).parse()),
            Matchers.allOf(
                Matchers.<Defect>iterableWithSize(1),
                Matchers.<Defect>everyItem(new DefectMatcher())
            )
        );
    }

    @Test
    void complainsAsError() throws IOException {
        final String src = LtSyntaxVersionTest.program("+syntax 999.0.0");
        MatcherAssert.assertThat(
            "the lint should complain as error, since the code may rely on unknown syntax",
            new LtSyntaxVersion().defects(new EoProgram(src, new InputOf(src)).parse())
                .iterator().next().severity(),
            Matchers.equalTo(Severity.ERROR)
        );
    }

    @Test
    void ignoresMalformedSyntaxValue() throws IOException {
        final String src = LtSyntaxVersionTest.program("+syntax abracadabra");
        MatcherAssert.assertThat(
            "a malformed +syntax value must not crash the lint, nor be flagged",
            new LtSyntaxVersion().defects(new EoProgram(src, new InputOf(src)).parse()),
            Matchers.emptyIterable()
        );
    }

    @Test
    void ignoresProgramsWithoutSyntaxMeta() throws IOException {
        final String src = LtSyntaxVersionTest.program("+home https://example.com");
        MatcherAssert.assertThat(
            "programs without a +syntax meta must not be affected",
            new LtSyntaxVersion().defects(new EoProgram(src, new InputOf(src)).parse()),
            Matchers.emptyIterable()
        );
    }

    @Test
    void explainsMotive() throws IOException {
        MatcherAssert.assertThat(
            "the motive doesn't mention the +syntax meta",
            new LtSyntaxVersion().motive().contains("+syntax"),
            new IsEqual<>(true)
        );
    }

    private static String actualVersion() {
        final String src = LtSyntaxVersionTest.program("+home https://example.com");
        return new Xnav(new EoProgram(src, new InputOf(src)).parse().inner())
            .path("/object").findFirst().get()
            .attribute("version").text().get();
    }

    private static String program(final String meta) {
        return String.join(System.lineSeparator(), meta, "", "[] > foo", "");
    }
}
