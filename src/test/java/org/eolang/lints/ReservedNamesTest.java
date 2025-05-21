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
 * Tests for {@link ReservedNames}.
 * The tests should be executed only in deep profile, since, we reserved name processing
 * happens only in that profile.
 * @since 0.0.49
 */
@Tag("deep")
final class ReservedNamesTest {

    @Test
    void findsReserved() {
        MatcherAssert.assertThat(
            "Reserved objects are empty, but they should not",
            new ReservedNames("target/classes/reserved.csv"),
            Matchers.aMapWithSize(Matchers.greaterThan(0))
        );
    }

    @Test
    void readsReservedInCorrectFormat() {
        MatcherAssert.assertThat(
            "Reserved names do not match with expected format",
            new ReservedNames("target/classes/reserved.csv").values(),
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
