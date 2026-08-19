/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link Listing}.
 * @since 0.0.53
 */
final class ListingTest {

    @Test
    void findsLineOfFirstCommentLine() {
        MatcherAssert.assertThat(
            "The first comment line must be found on the first line of the listing",
            new Listing(
                String.format("# Foo.%n%n[] > app%n")
            ).line("Foo.", 0).get(),
            Matchers.equalTo(1)
        );
    }

    @Test
    void findsLineOfSecondCommentLine() {
        MatcherAssert.assertThat(
            "The second comment line must be located after the first newline",
            new Listing(
                String.format("# first%n# second%n[] > app%n")
            ).line(
                "first".concat(System.lineSeparator()).concat("second"),
                8
            ).get(),
            Matchers.equalTo(2)
        );
    }

    @Test
    void returnsEmptyForMissingComment() {
        MatcherAssert.assertThat(
            "A comment not present in the listing must give an empty result",
            new Listing(String.format("[] > app%n")).line("missing", 0),
            Matchers.equalTo(java.util.Optional.empty())
        );
    }
}
