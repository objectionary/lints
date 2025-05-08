/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import java.nio.file.Paths;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link ReserveHome}.
 *
 * @since 0.0.49
 */
@Tag("deep")
final class ReserveHomeTest {

    @Test
    void placesHomeObjectsAsReserved() {
        final String csv = "target/classes/reserved.csv";
        new ReserveHome(csv).exec(ReserveHomeTest.homeLocation());
        MatcherAssert.assertThat(
            "Reserved objects are empty, but they should not",
            new ReservedNames(csv).value(),
            Matchers.aMapWithSize(Matchers.greaterThan(0))
        );
    }

    @Test
    void placesHomeObjectsWithCorrectNames() {
        final String csv = "target/classes/reserved.csv";
        new ReserveHome(csv).exec(ReserveHomeTest.homeLocation());
        MatcherAssert.assertThat(
            "Home objects do not match with expected format",
            new ReservedNames(csv).value().values(),
            Matchers.everyItem(
                Matchers.hasToString(
                    Matchers.matchesRegex(
                        "org\\.eolang(?:\\.[a-zA-Z_][a-zA-Z0-9_-]*)+\\.eo"
                    )
                )
            )
        );
    }

    /**
     * Home location.
     *
     * @return Home objects path as string
     */
    private static String homeLocation() {
        return Paths.get("downloaded", "home").toString();
    }
}
