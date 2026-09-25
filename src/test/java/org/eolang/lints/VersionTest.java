/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import java.util.Optional;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Version}.
 *
 * @since 0.0.59
 */
final class VersionTest {

    @Test
    void parsesSimpleCore() {
        MatcherAssert.assertThat(
            "The major.minor.patch core must be parsed as-is",
            Version.parsed("1.2.3").map(Object::toString),
            Matchers.equalTo(Optional.of("1.2.3"))
        );
    }

    @Test
    void ignoresPreReleaseSuffixWhenComparingCore() {
        MatcherAssert.assertThat(
            "A pre-release suffix must not change the numeric core",
            Version.parsed("1.2.3-alpha.1").map(Object::toString),
            Matchers.equalTo(Optional.of("1.2.3"))
        );
    }

    @Test
    void trimsSurroundingWhitespace() {
        MatcherAssert.assertThat(
            "Leading and trailing whitespace must be trimmed before parsing",
            Version.parsed("  1.2.3  ").map(Object::toString),
            Matchers.equalTo(Optional.of("1.2.3"))
        );
    }

    @Test
    void returnsEmptyForNonNumericInput() {
        MatcherAssert.assertThat(
            "A non-numeric string is not a version",
            Version.parsed("not-a-version").isPresent(),
            Matchers.equalTo(false)
        );
    }

    @Test
    void returnsEmptyForEmptyString() {
        MatcherAssert.assertThat(
            "An empty string is not a version",
            Version.parsed("").isPresent(),
            Matchers.equalTo(false)
        );
    }

    @Test
    void returnsEmptyWhenNumericComponentOverflows() {
        MatcherAssert.assertThat(
            "A component beyond int range must not throw, but be treated as unparsable",
            Version.parsed("999999999999999999999.0.0").isPresent(),
            Matchers.equalTo(false)
        );
    }

    @Test
    void formatsAsMajorMinorPatch() {
        MatcherAssert.assertThat(
            "toString() must render the exact major.minor.patch form",
            new Version(4, 5, 6).toString(),
            Matchers.equalTo("4.5.6")
        );
    }

    @Test
    void isNewerWhenMajorIsGreater() {
        MatcherAssert.assertThat(
            "A higher major version is newer, regardless of minor/patch",
            new Version(2, 0, 0).newerThan(new Version(1, 9, 9)),
            Matchers.equalTo(true)
        );
    }

    @Test
    void isNotNewerWhenMajorIsSmaller() {
        MatcherAssert.assertThat(
            "A lower major version is never newer",
            new Version(1, 9, 9).newerThan(new Version(2, 0, 0)),
            Matchers.equalTo(false)
        );
    }

    @Test
    void isNewerWhenMinorIsGreaterAtSameMajor() {
        MatcherAssert.assertThat(
            "A higher minor version is newer when major is equal",
            new Version(1, 2, 0).newerThan(new Version(1, 1, 9)),
            Matchers.equalTo(true)
        );
    }

    @Test
    void isNotNewerWhenMinorIsSmallerAtSameMajor() {
        MatcherAssert.assertThat(
            "A lower minor version is not newer when major is equal",
            new Version(1, 1, 0).newerThan(new Version(1, 2, 0)),
            Matchers.equalTo(false)
        );
    }

    @Test
    void isNewerWhenPatchIsGreaterAtSameMajorMinor() {
        MatcherAssert.assertThat(
            "A higher patch version is newer when major and minor are equal",
            new Version(1, 1, 2).newerThan(new Version(1, 1, 1)),
            Matchers.equalTo(true)
        );
    }

    @Test
    void isNotNewerWhenPatchIsSmallerAtSameMajorMinor() {
        MatcherAssert.assertThat(
            "A lower patch version is not newer when major and minor are equal",
            new Version(1, 1, 1).newerThan(new Version(1, 1, 2)),
            Matchers.equalTo(false)
        );
    }

    @Test
    void isNotNewerWhenAllComponentsAreEqual() {
        MatcherAssert.assertThat(
            "An identical version must never be newer than itself",
            new Version(1, 2, 3).newerThan(new Version(1, 2, 3)),
            Matchers.equalTo(false)
        );
    }
}
