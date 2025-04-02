/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import java.io.IOException;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.eolang.parser.EoSyntax;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link LtIncorrectNumberOfAttrs}.
 * @since 0.0.43
 */
final class LtIncorrectNumberOfAttrsTest {

    @Test
    void catchesIncorrectNumberOfAttributes() throws IOException {
        MatcherAssert.assertThat(
            "Defects are empty, but they should not",
            new LtIncorrectNumberOfAttrs().defects(
                new MapOf<String, XML>(
                    new MapEntry<>(
                        "foo",
                        new EoSyntax(
                            String.join(
                                "\n",
                                "# Foo with one attribute.",
                                "[a] > foo"
                            )
                        ).parsed()
                    ),
                    new MapEntry<>(
                        "app",
                        new EoSyntax(
                            String.join(
                                "\n",
                                "# App uses foo with two attributes instead.",
                                "[a b] > app",
                                "  foo a b"
                            )
                        ).parsed()
                    )
                )
            ),
            Matchers.hasSize(Matchers.greaterThan(0))
        );
    }

    @Test
    void catchesMultipleHighLevelObjectsInFile() throws IOException {
        MatcherAssert.assertThat(
            "Defects are empty, but they should not",
            new LtIncorrectNumberOfAttrs().defects(
                new MapOf<String, XML>(
                    new MapEntry<>(
                        "std",
                        new EoSyntax(
                            String.join(
                                "\n",
                                "# Std.",
                                "[a] > std",
                                "",
                                "# Stf.",
                                "[] > stf"
                            )
                        ).parsed()
                    ),
                    new MapEntry<>(
                        "usage",
                        new EoSyntax(
                            String.join(
                                "\n",
                                "# App uses std and stf.",
                                "[args] > app",
                                "  std 0",
                                "  stf 1"
                            )
                        ).parsed()
                    )
                )
            ),
            Matchers.hasSize(Matchers.greaterThan(0))
        );
    }

    @Test
    void allowsCorrectNumberOfAttributes() throws IOException {
        MatcherAssert.assertThat(
            "Defects are not empty, but they should",
            new LtIncorrectNumberOfAttrs().defects(
                new MapOf<String, XML>(
                    new MapEntry<>(
                        "a",
                        new EoSyntax(
                            String.join(
                                "\n",
                                "# A with one attribute.",
                                "[pos] > a"
                            )
                        ).parsed()
                    ),
                    new MapEntry<>(
                        "app",
                        new EoSyntax(
                            String.join(
                                "\n",
                                "# App uses a with correct number of arguments.",
                                "[] > app",
                                "  a 0"
                            )
                        ).parsed()
                    )
                )
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void allowsCorrectAttributesCountInSameFile() throws IOException {
        MatcherAssert.assertThat(
            "Defects are not empty, but they should",
            new LtIncorrectNumberOfAttrs().defects(
                new MapOf<>(
                    new MapEntry<>(
                        "single",
                        new EoSyntax(
                            String.join(
                                "\n",
                                "# A with one attribute.",
                                "[pos] > a",
                                "",
                                "# B uses A with one attribute",
                                "[] > b",
                                "  a 52"
                            )
                        ).parsed()
                    )
                )
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void catchesIncorrectPassedNumberOfAttributesInSameFile() throws IOException {
        MatcherAssert.assertThat(
            "Defects are empty, but they should not",
            new LtIncorrectNumberOfAttrs().defects(
                new MapOf<>(
                    new MapEntry<>(
                        "single-broken",
                        new EoSyntax(
                            String.join(
                                "\n",
                                "+package broken",
                                "",
                                "# F with one attribute.",
                                "[pos] > f",
                                "",
                                "# Z uses F with two attributes",
                                "[] > z",
                                "  f 52 42"
                            )
                        ).parsed()
                    )
                )
            ),
            Matchers.hasSize(Matchers.greaterThan(0))
        );
    }

    @Test
    void ignoresUndefinedObjectInPackage() throws IOException {
        MatcherAssert.assertThat(
            "Defects should empty, since object is not defined in provided package",
            new LtIncorrectNumberOfAttrs().defects(
                new MapOf<String, XML>(
                    new MapEntry<>(
                        "hello",
                        new EoSyntax(
                            String.join(
                                "\n",
                                "# Say hello.",
                                "[content] > hello"
                            )
                        ).parsed()
                    ),
                    new MapEntry<>(
                        "app",
                        new EoSyntax(
                            String.join(
                                "\n",
                                "# App uses undeclared object.",
                                "[] > app",
                                "  hello \"f\"",
                                "  bye 0x1"
                            )
                        ).parsed()
                    )
                )
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void understandsPackages() throws IOException {
        MatcherAssert.assertThat(
            "Defects should be empty, since object is expected to be packaged",
            new LtIncorrectNumberOfAttrs().defects(
                new MapOf<>(
                    new MapEntry<>(
                        "foo-unpackaged",
                        new EoSyntax(
                            String.join(
                                "\n",
                                "# An unpackaged foo.",
                                "[] > foo"
                            )
                        ).parsed()
                    ),
                    new MapEntry<>(
                        "foo-packaged",
                        new EoSyntax(
                            String.join(
                                "\n",
                                "+package f",
                                "",
                                "# Packaged foo in f.",
                                "[bar] > foo"
                            )
                        ).parsed()
                    ),
                    new MapEntry<>(
                        "res",
                        new EoSyntax(
                            String.join(
                                "\n",
                                "+alias f.foo",
                                "",
                                "# Resolver application that uses f.foo.",
                                "[args] > app",
                                "  foo args > @"
                            )
                        ).parsed()
                    )
                )
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void allowsCorrectAttributesInVerticalApplication() throws IOException {
        MatcherAssert.assertThat(
            "Defects should be empty, since attributes are correct in vertical application",
            new LtIncorrectNumberOfAttrs().defects(
                new MapOf<>(
                    new MapEntry<>(
                        "a",
                        new EoSyntax(
                            String.join(
                                "\n",
                                "# A with one attribute.",
                                "[pos] > a",
                                ""
                            )
                        ).parsed()
                    ),
                    new MapEntry<>(
                        "b",
                        new EoSyntax(
                            String.join(
                                "\n",
                                "# B with two attributes.",
                                "[left right] > b"
                            )
                        ).parsed()
                    ),
                    new MapEntry<>(
                        "usage",
                        new EoSyntax(
                            String.join(
                                "\n",
                                "# Usage of A and B objects with vertical application.",
                                "[] > app",
                                "  b",
                                "    0",
                                "    a 0"
                            )
                        ).parsed()
                    )
                )
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void catchesInCorrectAttributesInVerticalApplication() throws IOException {
        MatcherAssert.assertThat(
            "Defects should not be empty, since attributes are passed incorrectly",
            new LtIncorrectNumberOfAttrs().defects(
                new MapOf<>(
                    new MapEntry<>(
                        "x",
                        new EoSyntax(
                            String.join(
                                "\n",
                                "# X with one attribute.",
                                "[pos sigma] > x",
                                ""
                            )
                        ).parsed()
                    ),
                    new MapEntry<>(
                        "y",
                        new EoSyntax(
                            String.join(
                                "\n",
                                "# Y with two attributes.",
                                "[left right] > y"
                            )
                        ).parsed()
                    ),
                    new MapEntry<>(
                        "xy-app",
                        new EoSyntax(
                            String.join(
                                "\n",
                                "# Usage of X and Y objects with vertical application.",
                                "[] > app",
                                "  y",
                                "    1",
                                "    x 0"
                            )
                        ).parsed()
                    )
                )
            ),
            Matchers.hasSize(Matchers.greaterThan(0))
        );
    }
}
