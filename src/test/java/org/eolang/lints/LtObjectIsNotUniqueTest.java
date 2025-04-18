/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import matchers.DefectMatcher;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.eolang.parser.EoSyntax;
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
                        new EoSyntax(
                            "# Foo\n[] > foo"
                        ).parsed()
                    ),
                    new MapEntry<>(
                        "bar-with-foo",
                        new EoSyntax(
                            "# Bar\n[] > foo"
                        ).parsed()
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
    void allowsAllUnique() throws Exception {
        MatcherAssert.assertThat(
            "Defects aren't empty, but they should",
            new LtObjectIsNotUnique().defects(
                new MapOf<String, XML>(
                    new MapEntry<>(
                        "c",
                        new EoSyntax(
                            String.join(
                                "\n",
                                "# 42",
                                "[] > c",
                                "  42 > @"
                            )
                        ).parsed()
                    ),
                    new MapEntry<>(
                        "e",
                        new EoSyntax(
                            String.join(
                                "\n",
                                "# 52",
                                "[] > e",
                                "  52 > @"
                            )
                        ).parsed()
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
                        new EoSyntax(
                            String.join(
                                "\n",
                                "# Baz",
                                "[] > baz"
                            )
                        ).parsed()
                    ),
                    new MapEntry<>(
                        "baz-packaged",
                        new EoSyntax(
                            String.join(
                                "\n",
                                "+package utils",
                                "",
                                "# Baz",
                                "[] > baz"
                            )
                        ).parsed()
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
