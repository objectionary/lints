/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
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
                new MapOf<>(new MapEntry<>("foo", new ListOf<>(42, 43))), new ListOf<>()
            ).apply("foo:42"),
            Matchers.equalTo(false)
        );
    }

    @Test
    void returnsTrueWhenDoesNotMatch() {
        MatcherAssert.assertThat(
            "Input should match",
            new DefectMissing(
                new MapOf<>(new MapEntry<>("bar", new ListOf<>(42, 43))), new ListOf<>()
            ).apply("foo:52"),
            Matchers.equalTo(true)
        );
    }

    @Test
    void returnsTrueWhenLineOutOfRange() {
        MatcherAssert.assertThat(
            "Defect should be missing, but it was not",
            new DefectMissing(new MapOf<>("app", new ListOf<>(1, 3, 5)), new ListOf<>()).apply(
                "app:6-15"
            ),
            Matchers.equalTo(true)
        );
    }

    @Test
    void returnsFalseWhenLineInTheRange() {
        MatcherAssert.assertThat(
            "Defect should not be missing, but it was",
            new DefectMissing(new MapOf<>("div-by-zero", new ListOf<>(3, 6)), new ListOf<>())
                .apply("div-by-zero:3-6"),
            Matchers.equalTo(false)
        );
    }

    @Test
    void returnsFalseWhenRangeCoversTheLine() {
        MatcherAssert.assertThat(
            "Defect should not be missing, but it was",
            new DefectMissing(new MapOf<>("something", new ListOf<>(3, 6)), new ListOf<>())
                .apply("something:3-10"),
            Matchers.equalTo(false)
        );
    }

    @Test
    void returnsTrueIfSomeLineIsOutOfRange() {
        MatcherAssert.assertThat(
            "Defect should be missing, but it was not",
            new DefectMissing(new MapOf<>("noisy-lint", new ListOf<>(4, 6)), new ListOf<>())
                .apply("noisy-lint:6-10"),
            Matchers.equalTo(true)
        );
    }
}
