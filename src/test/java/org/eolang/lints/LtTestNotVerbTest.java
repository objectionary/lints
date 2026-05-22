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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests for {@link LtTestNotVerb}.
 * @since 0.0.22
 * @todo #872:60min Extract name-validation logic from LtTestNotVerb into a testable component.
 *  Currently {@link LtTestNotVerbTest} parses many EO programs that differ only in the
 *  object name being tested, making the tests slow and hard to maintain. The name-validation
 *  predicate (verb vs. non-verb check) should be extracted into its own class so it can be
 *  tested directly with plain strings — no EO parsing required. Once extracted, reduce
 *  {@link LtTestNotVerbTest} to a few representative end-to-end cases and add a dedicated
 *  unit test class for the new component.
 */
final class LtTestNotVerbTest {

    @ExtendWith(MayBeSlow.class)
    @ParameterizedTest
    @ValueSource(
        strings = {
            "itIsTrue",
            "testing",
            "this-is-test",
            "it-works",
            "nothing-happened",
            "something-is-wrong",
            "will-fail-eventually",
            "always-returns-true",
            "was-lost-forever",
            "nobody-knows-why",
            "should-not-pass",
            "once-upon-a-time",
            "must-do-better",
            "was-a-trap",
            "dont-look-here",
            "never-saw-it-coming",
            "this-time-for-sure",
            "there-it-goes",
            "it-is-fine-probably",
            "maybe-next-time",
            "well-this-is-awkward",
            "why-this-again",
            "here-we-go-again",
            "it-was-working-before",
            "expected-the-unexpected",
            "it-seems-fine",
            "suddenly-works",
            "too-late-now",
            "could-not-care-less",
            "it-just-works",
            "dont-push-that-button",
            "error-404-not-found",
            "who-did-this",
            "it-has-a-plan",
            "will-never-finish",
            "accidentally-passed",
            "i-think-its-ok",
            "chicken-as-expected",
            "please-reboot",
            "hope-it-works"
        }
    )
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
    @ParameterizedTest
    @ValueSource(
        strings = {
            "generates-report",
            "locks-branch",
            "parses-dom",
            "prints-data",
            "runs",
            "works-as-expected",
            "breaks-hearts",
            "crashes-again",
            "forgets-everything",
            "has-been-found",
            "looks-fine-to-me",
            "returns-something-strange",
            "disappears-silently",
            "follows-the-rules",
            "finds-nothing-at-all",
            "sounds-legit",
            "sleeps-forever",
            "makes-zero-sense",
            "runs-in-circles",
            "is-never-called",
            "is-kind-of-slow",
            "is-totally-broken",
            "is-almost-correct"
        }
    )
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
