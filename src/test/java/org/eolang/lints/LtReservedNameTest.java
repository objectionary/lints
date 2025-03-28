/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import java.io.IOException;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.eolang.parser.EoSyntax;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link LtReservedName}.
 *
 * @since 0.0.43
 */
final class LtReservedNameTest {

    @Test
    void catchesReservedName() throws IOException {
        MatcherAssert.assertThat(
            "Defects are empty, but they should not",
            new LtReservedName(new MapOf<>("true", "org.eolang.true.eo"))
                .defects(
                    new EoSyntax(
                        "foo",
                        String.join(
                            "\n",
                            "# Foo.",
                            "[] > foo",
                            "  42 > true"
                        )
                    ).parsed()
                ),
            Matchers.hasSize(Matchers.greaterThan(0))
        );
    }

    @Test
    void allowsNonReservedName() throws IOException {
        MatcherAssert.assertThat(
            "Defects are not empty, but they should",
            new LtReservedName(new MapOf<>("true", "org.eolang.true.eo"))
                .defects(
                    new EoSyntax(
                        "x",
                        String.join(
                            "# X object.",
                            "[] > x",
                            "  42 > y"
                        )
                    ).parsed()
                ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void allowsUniqueNameInTopProgramObject() throws IOException {
        MatcherAssert.assertThat(
            "Defects are not empty, but they should",
            new LtReservedName(new MapOf<>("f", "org.eolang.f.eo"))
                .defects(
                    new EoSyntax(
                        "top",
                        String.join(
                            "# top.",
                            "[] > top",
                            "  52 > x"
                        )
                    ).parsed()
                ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void catchesReservedNameWithPackage() throws IOException {
        MatcherAssert.assertThat(
            "There was no defects, though object name is already reserved",
            new LtReservedName(new MapOf<>("stdout", "org.eolang.stdout.eo"))
                .defects(
                    new EoSyntax(
                        "t-packaged",
                        String.join(
                            "+package org.foo",
                            "",
                            "# Tee packaged.",
                            "[] > tee",
                            "  52 > stdout"
                        )
                    ).parsed()
                ),
            Matchers.hasSize(Matchers.greaterThan(0))
        );
    }

    @Test
    void catchesMultipleNames() throws IOException {
        MatcherAssert.assertThat(
            "Defects should be caught",
            new LtReservedName(
                new MapOf<String, String>(
                    new MapEntry<>("left", "org.eolang.left.eo"),
                    new MapEntry<>("right", "org.eolang.right.eo")
                )
            ).defects(
                new EoSyntax(
                    "two-defects",
                    String.join(
                        "\n",
                        "# Left.",
                        "[] > left",
                        "",
                        "# Right.",
                        "[] > right"
                    )
                ).parsed()
            ),
            Matchers.hasSize(2)
        );
    }

    @Test
    void reportsAll() throws IOException {
        MatcherAssert.assertThat(
            "Defects are empty, but they should not",
            new LtReservedName(
                new MapOf<>(
                    new MapEntry<>("ja", "org.eolang.ja.eo"),
                    new MapEntry<>("jp", "org.eolang.jp.eo"),
                    new MapEntry<>("spb", "org.eolang.spb.eo")
                )
            ).defects(
                new EoSyntax(
                    "tops",
                    String.join(
                        "\n",
                        "# JA.",
                        "[] > ja",
                        "  52 > spb",
                        "",
                        "# JP.",
                        "[] > jp"
                    )
                ).parsed()
            ),
            Matchers.hasSize(3)
        );
    }

    @Test
    void reportsReservedNameInTopObject() throws IOException {
        MatcherAssert.assertThat(
            "Defects should be caught",
            new LtReservedName(new MapOf<>("foo", "org.eolang.foo.eo")).defects(
                new EoSyntax(
                    "foo",
                    String.join(
                        "\n",
                        "# Foo.",
                        "[] > foo",
                        "  52 > spb"
                    )
                ).parsed()
            ),
            Matchers.hasSize(Matchers.greaterThan(0))
        );
    }

    @Tag("deep")
    @Test
    void scansReservedFromHome() throws IOException {
        MatcherAssert.assertThat(
            "Defects are empty, but they should not",
            new LtReservedName().defects(
                new EoSyntax(
                    "foo",
                    String.join(
                        "\n",
                        "# Foo",
                        "[] > foo",
                        "  52 > stdout"
                    )
                ).parsed()
            ),
            Matchers.hasSize(Matchers.greaterThan(0))
        );
    }
}
