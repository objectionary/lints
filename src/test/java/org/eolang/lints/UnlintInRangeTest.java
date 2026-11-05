/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Tests for {@link UnlintInRange}.
 *
 * @since 0.0.54
 */
final class UnlintInRangeTest {

    @ParameterizedTest
    @CsvSource(
        {
            "foo:1-3,1,true",
            "app:1-3,2,true",
            "x:1-3,3,true",
            "xyz:1-3,4,false",
            "bar:55-199,200,false",
            "jiang:32-42,13,false"
        }
    )
    void returnsTrueIfLineInRange(
        final String unlint, final Integer line, final boolean expected
    ) {
        MatcherAssert.assertThat(
            "The result does not match with expected",
            new UnlintInRange(unlint).test(line),
            Matchers.equalTo(expected)
        );
    }

    @Test
    void returnsFalseWhenRangeBoundsDoNotFitIntegers() {
        MatcherAssert.assertThat(
            "An oversized range must not abort lint analysis",
            new UnlintInRange("foo:2147483648-2147483649").test(42),
            Matchers.equalTo(false)
        );
    }

    @Test
    void returnsFalseWhenUnlintHasNoRangeSeparator() {
        MatcherAssert.assertThat(
            "A single line selector must not throw index out of bounds",
            new UnlintInRange("foo:42").test(42),
            Matchers.equalTo(false)
        );
    }

    @Test
    void returnsFalseWhenUnlintHasSingleNumberOnly() {
        MatcherAssert.assertThat(
            "A numeric-only selector must not throw index out of bounds",
            new UnlintInRange("42").test(42),
            Matchers.equalTo(false)
        );
    }

    @Test
    void returnsFalseWhenUnlintIsEmpty() {
        MatcherAssert.assertThat(
            "An empty unlint selector must return false safely",
            new UnlintInRange("").test(1),
            Matchers.equalTo(false)
        );
    }

    @Test
    void returnsFalseWhenUnlintHasTrailingDashOnly() {
        MatcherAssert.assertThat(
            "An unlint with missing upper bound must return false safely",
            new UnlintInRange("foo:10-").test(10),
            Matchers.equalTo(false)
        );
    }

    @Test
    void returnsFalseWhenUnlintHasLeadingDashOnly() {
        MatcherAssert.assertThat(
            "An unlint with missing lower bound must return false safely",
            new UnlintInRange("foo:-10").test(10),
            Matchers.equalTo(false)
        );
    }
}
