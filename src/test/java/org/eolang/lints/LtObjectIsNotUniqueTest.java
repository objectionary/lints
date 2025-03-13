/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import fixtures.ParsedEo;
import matchers.DefectMatcher;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link LtObjectIsNotUnique}.
 *
 * @since 0.0.30
 */
final class LtObjectIsNotUniqueTest {

    @Test
    void catchesDuplicates() throws Exception {
        MatcherAssert.assertThat(
            "Defects are empty, but they should not",
            new LtObjectIsNotUnique().defects(
                new MapOf<String, XML>(
                    new MapEntry<>(
                        "foo",
                        new ParsedEo(
                            "org/eolang/lints/errors/object-is-not-unique/foo.eo"
                        ).value()
                    ),
                    new MapEntry<>(
                        "bar-with-foo",
                        new ParsedEo(
                            "org/eolang/lints/errors/object-is-not-unique/bar-with-foo.eo"
                        ).value()
                    )
                )
            ),
            Matchers.allOf(
                Matchers.<Defect>iterableWithSize(Matchers.greaterThan(0)),
                Matchers.<Defect>everyItem(new DefectMatcher())
            )
        );
    }

    @Test
    void catchesDuplicatesAcrossMultipleObjects() throws Exception {
        MatcherAssert.assertThat(
            "Defects are empty, but they should not",
            new LtObjectIsNotUnique().defects(
                new MapOf<String, XML>(
                    new MapEntry<>(
                        "test-1",
                        new ParsedEo(
                            "org/eolang/lints/errors/object-is-not-unique/test-1.eo"
                        ).value()
                    ),
                    new MapEntry<>(
                        "test-2",
                        new ParsedEo(
                            "org/eolang/lints/errors/object-is-not-unique/test-2.eo"
                        ).value()
                    )
                )
            ),
            Matchers.hasSize(Matchers.greaterThan(0))
        );
    }

    @Test
    void allowsAllUnique() throws Exception {
        MatcherAssert.assertThat(
            "Defects aren't empty, but they should",
            new LtObjectIsNotUnique().defects(
                new MapOf<String, XML>(
                    new MapEntry<>(
                        "c",
                        new ParsedEo(
                            "org/eolang/lints/errors/object-is-not-unique/c.eo"
                        ).value()
                    ),
                    new MapEntry<>(
                        "e",
                        new ParsedEo(
                            "org/eolang/lints/errors/object-is-not-unique/e.eo"
                        ).value()
                    )
                )
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void allowsNonUniqueInDifferentPackages() throws Exception {
        MatcherAssert.assertThat(
            "Defects aren't empty, but they should",
            new LtObjectIsNotUnique().defects(
                new MapOf<String, XML>(
                    new MapEntry<>(
                        "baz",
                        new ParsedEo(
                            "org/eolang/lints/errors/object-is-not-unique/baz.eo"
                        ).value()
                    ),
                    new MapEntry<>(
                        "baz-packaged",
                        new ParsedEo(
                            "org/eolang/lints/errors/object-is-not-unique/baz-packaged.eo"
                        ).value()
                    )
                )
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void allowsNonUniqueMultipleObjectsInDifferentPackages() throws Exception {
        MatcherAssert.assertThat(
            "Defects aren't empty, but they should",
            new LtObjectIsNotUnique().defects(
                new MapOf<String, XML>(
                    new MapEntry<>(
                        "mul",
                        new ParsedEo(
                            "org/eolang/lints/errors/object-is-not-unique/mul.eo"
                        ).value()
                    ),
                    new MapEntry<>(
                        "mul-packaged",
                        new ParsedEo(
                            "org/eolang/lints/errors/object-is-not-unique/mul-packaged.eo"
                        ).value()
                    )
                )
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void doesNotThrowExceptionOnEmptyXmir() {
        Assertions.assertDoesNotThrow(
            () -> new LtObjectIsNotUnique().defects(
                new MapOf<>(
                    new MapEntry<>(
                        "empty",
                        new XMLDocument("<program/>")
                    )
                )
            ),
            "Exception was thrown, but it should not"
        );
    }
}
