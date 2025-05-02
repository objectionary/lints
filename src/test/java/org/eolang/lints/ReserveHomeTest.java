/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.yegor256.tojos.MnCsv;
import com.yegor256.tojos.TjCached;
import com.yegor256.tojos.TjDefault;
import com.yegor256.tojos.TjSynchronized;
import com.yegor256.tojos.Tojo;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
            ReserveHomeTest.reserved(csv),
            Matchers.aMapWithSize(Matchers.greaterThan(0))
        );
    }

    @Test
    void placesHomeObjectsWithCorrectNames() {
        final String csv = "target/classes/reserved.csv";
        new ReserveHome(csv).exec(ReserveHomeTest.homeLocation());
        MatcherAssert.assertThat(
            "Home objects do not match with expected format",
            ReserveHomeTest.reserved(csv).values(),
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
     * Fetch all reserved objects.
     *
     * @param path Path with reserved objects
     * @return Map of reserved objects
     */
    private static Map<String, String> reserved(final String path) {
        final List<Tojo> selected = new TjCached(
            new TjSynchronized(
                new TjDefault(
                    new MnCsv(path)
                )
            )
        ).select(tojo -> true);
        System.out.println(selected);
        final Map<String, String> collected = selected.stream()
            .map(Tojo::toMap)
            .flatMap(map -> map.entrySet().stream())
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (v1, v2) -> v2
                )
            );
        System.out.println(collected);
        return collected;
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
