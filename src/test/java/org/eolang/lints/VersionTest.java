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
 * @since 0.2.11
 */
final class VersionTest {

    @Test
    void parsesPlainVersion() {
        MatcherAssert.assertThat(
            "Plain version is not parsed",
            Version.parsed("1.2.3").map(Version::toString),
            Matchers.equalTo(Optional.of("1.2.3"))
        );
    }

    @Test
    void parsesPreReleaseByItsCore() {
        MatcherAssert.assertThat(
            "Pre-release is not compared by its numeric core",
            Version.parsed("1.2.3-alpha.1").map(Version::toString),
            Matchers.equalTo(Optional.of("1.2.3"))
        );
    }

    @Test
    void parsesBuildMetadataByItsCore() {
        MatcherAssert.assertThat(
            "Build metadata is not compared by its numeric core",
            Version.parsed("1.2.3+exp.sha.5114f85").map(Version::toString),
            Matchers.equalTo(Optional.of("1.2.3"))
        );
    }

    @Test
    void parsesPreReleaseWithBuildMetadata() {
        MatcherAssert.assertThat(
            "Pre-release with build metadata is not read by its core",
            Version.parsed("1.2.3-rc.1+exp.sha.1").map(Version::toString),
            Matchers.equalTo(Optional.of("1.2.3"))
        );
    }

    @Test
    void rejectsTrailingGarbage() {
        MatcherAssert.assertThat(
            "Trailing text must not be read as a version",
            Version.parsed("1.2.3garbage").isPresent(),
            Matchers.is(false)
        );
    }

    @Test
    void rejectsFourthComponent() {
        MatcherAssert.assertThat(
            "A fourth numeric component must not be ignored",
            Version.parsed("1.2.3.4").isPresent(),
            Matchers.is(false)
        );
    }

    @Test
    void rejectsTextGluedToPatch() {
        MatcherAssert.assertThat(
            "Text glued to the patch must not be read as a version",
            Version.parsed("999.0.0oops").isPresent(),
            Matchers.is(false)
        );
    }

    @Test
    void returnsEmptyWhenComponentOverflows() {
        MatcherAssert.assertThat(
            "A component above Integer.MAX_VALUE must not throw",
            Version.parsed("999999999999999999999999.2.3").isPresent(),
            Matchers.is(false)
        );
    }
}
