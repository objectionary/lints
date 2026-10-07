/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import org.cactoos.list.ListOf;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link DefectMissing}.
 *
 * @since 0.0.44
 */
final class DefectMissingTest {

    @Test
    void returnsFalseOnMatch() {
        MatcherAssert.assertThat(
            "Input should not match, since defect is present",
            new DefectMissing(
                new MapOf<>(new MapEntry<>("foo", new ListOf<>(42, 43)))
            ).apply("foo:42"),
            Matchers.equalTo(false)
        );
    }

    @Test
    void returnsTrueWhenDoesNotMatch() {
        MatcherAssert.assertThat(
            "Input should match",
            new DefectMissing(
                new MapOf<>(new MapEntry<>("bar", new ListOf<>(42, 43)))
            ).apply("foo:52"),
            Matchers.equalTo(true)
        );
    }

    @Test
    void returnsTrueForNonNumericLine() {
        MatcherAssert.assertThat(
            "Non-numeric line selector should be reported as missing",
            new DefectMissing(new MapOf<>("foo", new ListOf<>(42)))
                .apply("foo:not-a-number"),
            Matchers.equalTo(true)
        );
    }

    @Test
    void returnsTrueWhenLineOutOfRange() {
        MatcherAssert.assertThat(
            "Defect should be missing, but it was not",
            new DefectMissing(new MapOf<>("app", new ListOf<>(1, 3, 5))).apply(
                "app:6-15"
            ),
            Matchers.equalTo(true)
        );
    }

    @Test
    void returnsFalseWhenLineInTheRange() {
        MatcherAssert.assertThat(
            "Defect should not be missing, but it was",
            new DefectMissing(new MapOf<>("div-by-zero", new ListOf<>(3, 6)))
                .apply("div-by-zero:3-6"),
            Matchers.equalTo(false)
        );
    }

    @Test
    void returnsFalseWhenRangeCoversTheLine() {
        MatcherAssert.assertThat(
            "Defect should not be missing, but it was",
            new DefectMissing(new MapOf<>("something", new ListOf<>(3, 6)))
                .apply("something:3-10"),
            Matchers.equalTo(false)
        );
    }

    @Test
    void returnsFalseWhenSomeLineIsInTheRange() {
        MatcherAssert.assertThat(
            "Defect should not be missing, since at least one defect is covered by the range",
            new DefectMissing(new MapOf<>("noisy-lint", new ListOf<>(4, 6)))
                .apply("noisy-lint:6-10"),
            Matchers.equalTo(false)
        );
    }

    @Test
    void returnsTrueWhenRangeReferencesAbsentLint() {
        MatcherAssert.assertThat(
            "Defect should be missing when range pattern references an absent lint",
            new DefectMissing(new MapOf<>()).apply("ascii-only:1-5"),
            Matchers.equalTo(true)
        );
    }

    @Test
    void returnsTrueWhenSingleLineDoesNotFitAnInteger() {
        MatcherAssert.assertThat(
            "An oversized line selector should be treated as a missing defect",
            new DefectMissing(
                new MapOf<>(new MapEntry<>("foo", new ListOf<>(42)))
            ).apply("foo:2147483648"),
            Matchers.equalTo(true)
        );
    }

    @Test
    void treatsRegexCharactersInRuleNamesAsLiteralText() {
        MatcherAssert.assertThat(
            "Rule names must not be interpreted as regular expressions",
            new DefectMissing(new MapOf<>()).apply("[bad:1-2"),
            Matchers.equalTo(true)
        );
    }
}
