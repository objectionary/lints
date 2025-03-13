/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.yegor256.MayBeSlow;
import com.yegor256.Together;
import fixtures.ParsedEo;
import matchers.DefectMatcher;
import org.cactoos.set.SetOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Tests for {@link LtTestNotVerb}.
 *
 * @since 0.0.22
 */
final class LtTestNotVerbTest {

    @Test
    @ExtendWith(MayBeSlow.class)
    void catchesBadName() throws Exception {
        MatcherAssert.assertThat(
            "Defects size doesn't match with expected",
            new LtTestNotVerb().defects(
                new ParsedEo(
                    "org/eolang/lints/misc/test-object-is-not-verb-in-singular/bad-tests.eo"
                ).value()
            ),
            Matchers.allOf(
                Matchers.<Defect>iterableWithSize(40),
                Matchers.everyItem(new DefectMatcher())
            )
        );
    }

    @Test
    @ExtendWith(MayBeSlow.class)
    void allowsGoodNames() throws Exception {
        MatcherAssert.assertThat(
            "Defects are not empty, but they shouldn't be",
            new LtTestNotVerb().defects(
                new ParsedEo(
                    "org/eolang/lints/misc/test-object-is-not-verb-in-singular/good-tests.eo"
                ).value()
            ),
            Matchers.hasSize(0)
        );
    }

    @SuppressWarnings("JTCOP.RuleNotContainsTestWord")
    @Test
    @ExtendWith(MayBeSlow.class)
    void lintsRegexTests() throws Exception {
        MatcherAssert.assertThat(
            "Defects size doesn't match with expected",
            new LtTestNotVerb().defects(
                new ParsedEo(
                    "org/eolang/lints/misc/test-object-is-not-verb-in-singular/regex-tests.eo"
                ).value()
            ),
            Matchers.hasSize(12)
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
                        new ParsedEo(
                            "org/eolang/lints/misc/test-object-is-not-verb-in-singular/regex-tests.eo"
                        ).value()
                    ).size()
                ).asList()
            ).size(),
            Matchers.equalTo(1)
        );
    }
}
