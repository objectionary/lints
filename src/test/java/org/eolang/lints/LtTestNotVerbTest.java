/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.yegor256.MayBeSlow;
import com.yegor256.Together;
import fixtures.EoProgram;
import java.io.IOException;
import matchers.DefectMatcher;
import org.cactoos.io.InputOf;
import org.cactoos.set.SetOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests for {@link LtTestNotVerb}.
 * @since 0.0.22
 */
final class LtTestNotVerbTest {

    @ExtendWith(MayBeSlow.class)
    @Execution(ExecutionMode.SAME_THREAD)
    @ParameterizedTest
    @ValueSource(strings = {"it-works", "should-not-pass", "testing"})
    void catchesBadName(final String name) throws IOException {
        MatcherAssert.assertThat(
            "Defects size doesn't match with expected",
            new LtTestNotVerb().defects(
                new EoProgram(
                    name,
                    new InputOf(
                        String.join(
                            System.lineSeparator(),
                            "# Foo",
                            "[] > foo",
                            String.format("  [] +> %s", name),
                            "    42 > @"
                        )
                    )
                ).parse()
            ),
            Matchers.allOf(
                Matchers.<Defect>iterableWithSize(1),
                Matchers.everyItem(new DefectMatcher())
            )
        );
    }

    @ExtendWith(MayBeSlow.class)
    @Execution(ExecutionMode.SAME_THREAD)
    @ParameterizedTest
    @ValueSource(strings = {"generates-report", "runs", "parses-dom"})
    void allowsGoodNames(final String name) throws IOException {
        MatcherAssert.assertThat(
            "Defects are not empty, but they shouldn't be",
            new LtTestNotVerb().defects(
                new EoProgram(
                    name,
                    new InputOf(
                        String.join(
                            System.lineSeparator(),
                            "# Foo",
                            "[] > foo",
                            String.format("  [] +> %s", name),
                            "    42 > @"
                        )
                    )
                ).parse()
            ),
            Matchers.hasSize(0)
        );
    }

    @SuppressWarnings("JTCOP.RuleNotContainsTestWord")
    @Test
    @ExtendWith(MayBeSlow.class)
    void lintsRegexTests() throws IOException {
        MatcherAssert.assertThat(
            "Defects size doesn't match with expected",
            new LtTestNotVerb().defects(
                new EoProgram("org/eolang/lints/regex-test.eo").parse()
            ),
            Matchers.hasSize(1)
        );
    }

    @Tag("deep")
    @RepeatedTest(2)
    void lintsInMultipleThreads() {
        MatcherAssert.assertThat(
            "wrong number of defects found, in parallel",
            new SetOf<>(
                new Together<>(
                    t -> new LtTestNotVerb().defects(
                        new EoProgram("org/eolang/lints/regex-match.eo").parse()
                    ).size()
                ).asList()
            ).size(),
            Matchers.equalTo(1)
        );
    }
}
