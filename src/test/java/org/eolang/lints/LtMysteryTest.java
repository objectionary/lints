/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import fixtures.EoProgram;
import java.io.IOException;
import java.util.Map;
import org.cactoos.io.InputOf;
import org.cactoos.list.ListOf;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link LtMystery}.
 * @since 0.0.52
 */
final class LtMysteryTest {

    @Test
    void catchesMysteryObject() throws IOException {
        MatcherAssert.assertThat(
            "It is expected to catch a mystery object here",
            new LtMystery(this.canonical()).defects(
                new EoProgram("org/eolang/lints/mystery-object.eo").parse()
            ),
            Matchers.hasSize(1)
        );
    }

    @Test
    void allowsDeclaredObject() throws IOException {
        MatcherAssert.assertThat(
            "Defects are not empty, but the object is declared in the program",
            new LtMystery(this.canonical()).defects(
                new EoProgram("org/eolang/lints/declared-object.eo").parse()
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void allowsAliasedObject() throws IOException {
        MatcherAssert.assertThat(
            "Defects are not empty, but the object is imported via +alias",
            new LtMystery(this.canonical()).defects(
                new EoProgram("org/eolang/lints/aliased-object.eo").parse()
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void allowsCanonicalObject() throws IOException {
        MatcherAssert.assertThat(
            "Defects are not empty, but the object is part of org.eolang",
            new LtMystery(this.canonical()).defects(
                new EoProgram("org/eolang/lints/canonical-object.eo").parse()
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void reportsCorrectMessageForMysteryObject() throws IOException {
        MatcherAssert.assertThat(
            "The message should mention the mystery object",
            new ListOf<>(
                new LtMystery(this.canonical()).defects(
                    new EoProgram("org/eolang/lints/mystery-object.eo").parse()
                )
            ).get(0).text(),
            Matchers.equalTo(
                "Object \"bar\" is not defined in the program and is not part of org.eolang"
            )
        );
    }

    @Test
    void allowsAllObjectsDeclaredInline() throws IOException {
        final String src = String.format(
            "[] > foo%n  [a] > bar%n  bar > x"
        );
        MatcherAssert.assertThat(
            "Objects should not be reported, since all of them are declared",
            new LtMystery(this.canonical()).defects(
                new EoProgram(src, new InputOf(src)).parse()
            ),
            Matchers.emptyIterable()
        );
    }

    @Tag("reserved")
    @Test
    void scansMysteryFromHome() throws Exception {
        MatcherAssert.assertThat(
            "It is expected to catch a mystery object using reserved names from home",
            new LtMystery().defects(
                new EoProgram("org/eolang/lints/mystery-bipki.eo").parse()
            ),
            Matchers.hasSize(1)
        );
    }

    /**
     * Dummy reserved names for the tests.
     * @return Reserved names map
     */
    private Map<String, String> canonical() {
        return new MapOf<>(
            new MapEntry<>("number", "number.eo"),
            new MapEntry<>("bytes", "bytes.eo"),
            new MapEntry<>("string", "string.eo")
        );
    }
}
