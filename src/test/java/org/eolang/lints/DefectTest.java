/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Defect}.
 * @since 0.0.12
 */
final class DefectTest {

    @Test
    void returnsVersion() {
        MatcherAssert.assertThat(
            "Version doesn't match with expected",
            new Defect.Default(
                "metas/incorrect-architect",
                Severity.WARNING,
                3,
                "Something went wrong with an architect"
            ).version(),
            Matchers.equalTo("1.2.3")
        );
    }

    @Test
    void printsWithoutZeroLineNumber() {
        MatcherAssert.assertThat(
            "toString() prints zero for line number",
            new Defect.Default(
                "foo",
                Severity.WARNING,
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
                42,
                "This is wrong",
                true
            ).experimental(),
            Matchers.equalTo(true)
        );
    }
}
