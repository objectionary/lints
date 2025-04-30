/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Defect}.
 *
 * @since 0.0.12
 */
final class DefectTest {

    @Test
    void returnsVersion() {
        final String version = new Defect.Default(
            "metas/incorrect-architect",
            Severity.WARNING,
            "",
            3,
            "Something went wrong with an architect"
        ).version();
        MatcherAssert.assertThat(
            "Version doesn't match with expected",
            version,
            Matchers.equalTo("1.2.3")
        );
    }

    @Test
    void printsProgramName() {
        final String program = "a.b.c.bar";
        MatcherAssert.assertThat(
            "toString() doesn't show program name",
            new Defect.Default(
                "foo",
                Severity.WARNING,
                program,
                3,
                "the message"
            ),
            Matchers.hasToString(Matchers.containsString(program))
        );
    }

    @Test
    void printsWithoutZeroLineNumber() {
        MatcherAssert.assertThat(
            "toString() prints zero for line number",
            new Defect.Default(
                "foo",
                Severity.WARNING,
                "foo.bar",
                0,
                "the message"
            ),
            Matchers.hasToString(Matchers.not(Matchers.containsString(":0")))
        );
    }

    @Test
    void returnsExperimental() {
        MatcherAssert.assertThat(
            "Experimental flag should be true",
            new Defect.Default(
                "f12",
                Severity.ERROR,
                "f.12",
                42,
                "This is wrong",
                true
            ).experimental(),
            Matchers.equalTo(true)
        );
    }
}
