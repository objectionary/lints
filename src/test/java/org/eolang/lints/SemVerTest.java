/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests for {@link SemVer}.
 *
 * @since 1.0
 */
@SuppressWarnings("PMD.TooManyMethods")
final class SemVerTest {

    @Test
    void parsesSimpleVersion() {
        final SemVer ver = new SemVer("1.2.3");
        MatcherAssert.assertThat(
            "Major version doesn't match",
            ver.major(),
            Matchers.equalTo(1)
        );
        MatcherAssert.assertThat(
            "Minor version doesn't match",
            ver.minor(),
            Matchers.equalTo(2)
        );
        MatcherAssert.assertThat(
            "Patch version doesn't match",
            ver.patch(),
            Matchers.equalTo(3)
        );
    }

    @Test
    void parsesVersionWithPrerelease() {
        final SemVer ver = new SemVer("1.0.0-alpha.1");
        MatcherAssert.assertThat(
            "Pre-release doesn't match",
            ver.prerelease(),
            Matchers.equalTo("alpha.1")
        );
        MatcherAssert.assertThat(
            "Should be pre-release",
            ver.isPrerelease(),
            Matchers.equalTo(true)
        );
    }

    @Test
    void parsesVersionWithBuildMetadata() {
        final SemVer ver = new SemVer("1.0.0+20130313144700");
        MatcherAssert.assertThat(
            "Build metadata doesn't match",
            ver.metadata(),
            Matchers.equalTo("20130313144700")
        );
    }

    @Test
    void parsesVersionWithPrereleaseAndBuild() {
        final SemVer ver = new SemVer("1.0.0-beta+exp.sha.5114f85");
        MatcherAssert.assertThat(
            "Pre-release doesn't match",
            ver.prerelease(),
            Matchers.equalTo("beta")
        );
        MatcherAssert.assertThat(
            "Build metadata doesn't match",
            ver.metadata(),
            Matchers.equalTo("exp.sha.5114f85")
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "2.3.4",
        "0.0.0",
        "10.20.30",
        "1.0.0-alpha",
        "1.0.0-alpha.1",
        "1.0.0-0.3.7",
        "1.0.0-x.7.z.92",
        "1.0.0+20130313144700",
        "1.0.0-beta+exp.sha.5114f85",
        "1.0.0+21AF26D3----117B344092BD"
    })
    void parsesCorrectVersions(final String version) {
        MatcherAssert.assertThat(
            String.format("'%s' should parse without error", version),
            new SemVer(version).toString(),
            Matchers.equalTo(version)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "1.2",
        "1",
        "1.2.3.4",
        "01.2.3",
        "1.02.3",
        "1.2.03",
        "alpha.beta.gamma",
        "v1.2.3",
        "1.2.3-",
        "1.2.3+"
    })
    @SuppressWarnings("PMD.AvoidUsingHardCodedIP")
    void rejectsInvalidVersions(final String version) {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new SemVer(version),
            String.format("'%s' should be rejected", version)
        );
    }

    @Test
    void throwsOnInvalidParse() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new SemVer("not-a-version"),
            "Should throw on invalid version string"
        );
    }

    @Test
    void convertsToString() {
        MatcherAssert.assertThat(
            "toString doesn't produce correct output",
            new SemVer("1.2.3-alpha+build").toString(),
            Matchers.equalTo("1.2.3-alpha+build")
        );
    }

    @Test
    void convertsSimpleToString() {
        MatcherAssert.assertThat(
            "toString doesn't produce correct simple output",
            new SemVer(3, 4, 5).toString(),
            Matchers.equalTo("3.4.5")
        );
    }

    @Test
    void comparesMajorVersions() {
        MatcherAssert.assertThat(
            "1.0.0 should be less than 2.0.0",
            new SemVer("1.0.0").compareTo(new SemVer("2.0.0")),
            Matchers.equalTo(-1)
        );
    }

    @Test
    void comparesMinorVersions() {
        MatcherAssert.assertThat(
            "1.0.1 should be less than 1.1.0",
            new SemVer("1.0.1").compareTo(new SemVer("1.1.0")),
            Matchers.equalTo(-1)
        );
    }

    @Test
    void comparesPatchVersions() {
        MatcherAssert.assertThat(
            "1.0.4 should be less than 1.0.5",
            new SemVer("1.0.4").compareTo(new SemVer("1.0.5")),
            Matchers.equalTo(-1)
        );
    }

    @Test
    void treatsPrereleaseAsLessThanRelease() {
        MatcherAssert.assertThat(
            "Pre-release should be less than release",
            new SemVer("1.0.0-alpha").compareTo(new SemVer("1.0.0")),
            Matchers.equalTo(-1)
        );
    }

    @Test
    void comparesPrereleaseIdentifiers() {
        MatcherAssert.assertThat(
            "alpha should be less than beta",
            new SemVer("1.0.0-alpha"),
            Matchers.lessThan(new SemVer("1.0.0-beta"))
        );
    }

    @Test
    void comparesNumericPrereleaseIdentifiers() {
        MatcherAssert.assertThat(
            "beta.2 should be less than beta.11",
            new SemVer("1.0.0-beta.2"),
            Matchers.lessThan(new SemVer("1.0.0-beta.11"))
        );
    }

    @Test
    void followsSemverPrecedence() {
        MatcherAssert.assertThat(
            "alpha < alpha.1",
            new SemVer("1.0.0-alpha"),
            Matchers.lessThan(new SemVer("1.0.0-alpha.1"))
        );
        MatcherAssert.assertThat(
            "alpha.1 < alpha.beta",
            new SemVer("1.0.0-alpha.1"),
            Matchers.lessThan(new SemVer("1.0.0-alpha.beta"))
        );
        MatcherAssert.assertThat(
            "alpha.beta < beta",
            new SemVer("1.0.0-alpha.beta"),
            Matchers.lessThan(new SemVer("1.0.0-beta"))
        );
        MatcherAssert.assertThat(
            "beta < beta.2",
            new SemVer("1.0.0-beta"),
            Matchers.lessThan(new SemVer("1.0.0-beta.2"))
        );
        MatcherAssert.assertThat(
            "beta.2 < beta.11",
            new SemVer("1.0.0-beta.2"),
            Matchers.lessThan(new SemVer("1.0.0-beta.11"))
        );
        MatcherAssert.assertThat(
            "beta.11 < rc.1",
            new SemVer("1.0.0-beta.11"),
            Matchers.lessThan(new SemVer("1.0.0-rc.1"))
        );
    }

    @Test
    void treatsEqualVersionsAsEqual() {
        MatcherAssert.assertThat(
            "Same versions should be equal",
            new SemVer("1.6.1"),
            Matchers.equalTo(new SemVer("1.6.1"))
        );
    }

    @Test
    void ignoresBuildMetadataInComparison() {
        MatcherAssert.assertThat(
            "Build metadata should be ignored in comparison",
            new SemVer("1.0.0+build1"),
            Matchers.equalTo(new SemVer("1.0.0+build2"))
        );
    }

    @Test
    void identifiesNotPrereleaseWhenAbsent() {
        MatcherAssert.assertThat(
            "Should not be pre-release",
            new SemVer("1.0.0").isPrerelease(),
            Matchers.equalTo(false)
        );
    }

    @Test
    void producesConsistentHashCodes() {
        MatcherAssert.assertThat(
            "Equal versions should have equal hash codes",
            new SemVer("1.6.2").hashCode(),
            Matchers.equalTo(new SemVer("1.6.2").hashCode())
        );
    }
}
