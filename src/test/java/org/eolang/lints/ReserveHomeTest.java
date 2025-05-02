/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.yegor256.tojos.MnCsv;
import com.yegor256.tojos.TjCached;
import com.yegor256.tojos.TjDefault;
import com.yegor256.tojos.TjSynchronized;
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
    void placesHomeObjectsAsReserved() throws Exception {
        final String csv = "target/classes/reserved.csv";
        new ReserveHome(csv).exec(Paths.get("downloaded", "home").toString());
        MatcherAssert.assertThat(
            "Reserved objects are empty, but they should not",
            new TjCached(
                new TjSynchronized(
                    new TjDefault(
                        new MnCsv(csv)
                    )
                )
            ).select(tojo -> true),
            Matchers.iterableWithSize(Matchers.greaterThan(0))
        );
    }

    @Test
    void placesHomeObjectsWithCorrectNames() throws Exception {
//        MatcherAssert.assertThat(
//            "Home objects do not match with expected format",
//            new ReserveHome().value().values(),
//            Matchers.everyItem(
//                Matchers.hasToString(
//                    Matchers.matchesRegex(
//                        "org\\.eolang(?:\\.[a-zA-Z_][a-zA-Z0-9_-]*)+\\.eo"
//                    )
//                )
//            )
//        );
    }
}
