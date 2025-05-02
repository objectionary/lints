/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link HomeReserved}.
 *
 * @since 0.0.49
 */
@Tag("deep")
final class HomeReservedTest {

    @Test
    void placesHomeObjects() throws Exception {
        MatcherAssert.assertThat(
            "Home objects are empty, but they should not",
            new HomeReserved().value(),
            Matchers.aMapWithSize(Matchers.greaterThan(0))
        );
    }

    @Test
    void placesHomeObjectsWithCorrectNames() throws Exception {
        MatcherAssert.assertThat(
            "Home objects do not match with expected format",
            new HomeReserved().value().values(),
            Matchers.everyItem(
                Matchers.hasToString(
                    Matchers.matchesRegex(
                        "org\\.eolang(?:\\.[a-zA-Z_][a-zA-Z0-9_-]*)+\\.eo"
                    )
                )
            )
        );
    }
}
