/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Severity}.
 *
 * @since 0.0.12
 */
final class SeverityTest {

    @Test
    void returnsMnemoInLowCase() {
        MatcherAssert.assertThat(
            "Mnemo is in low-case",
            Severity.WARNING.mnemo(),
            Matchers.equalTo("warning")
        );
    }

    @Test
    void parsesStringForMnemo() {
        MatcherAssert.assertThat(
            "Can be parsed",
            Severity.parsed("warning"),
            Matchers.equalTo(Severity.WARNING)
        );
    }
}
