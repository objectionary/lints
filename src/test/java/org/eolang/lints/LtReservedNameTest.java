/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import java.io.IOException;
import java.util.Collection;
import org.cactoos.list.ListOf;
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
 * @since 0.0.44
 */
final class LtReservedNameTest {

    @Test
    void catchesReservedName() throws IOException {
        MatcherAssert.assertThat(
            "It is expected to catch only one defect here",
            new LtReservedName(new MapOf<>("true", "org.eolang.true.eo"))
                .defects(
                    new EoSyntax(
                        String.join(
                            "\n",
                            "# Qqq.",
                            "[] > qqq",
                            "  42 > true"
                        )
                    ).parsed()
                ),
            Matchers.hasSize(1)
        );
    }

    @Test
    void allowsNonReservedName() throws IOException {
        MatcherAssert.assertThat(
            "Defects are not empty, but they should",
            new LtReservedName(new MapOf<>("true", "org.eolang.true.eo"))
                .defects(
                    new EoSyntax(
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
    void allowsUniqueNameInTopSourceObject() throws IOException {
        MatcherAssert.assertThat(
            "Defects are not empty, but they should",
            new LtReservedName(new MapOf<>("f", "org.eolang.f.eo"))
                .defects(
                    new EoSyntax(
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
        final Collection<Defect> found = new LtReservedName(
            new MapOf<>("stdout", "org.eolang.stdout.eo")
        ).defects(
            new EoSyntax(
                String.join(
                    "+package org.foo",
                    "",
                    "# Tee packaged.",
                    "[] > tee",
                    "  52 > stdout"
                )
            ).parsed()
        );
        final int expected = 1;
        MatcherAssert.assertThat(
            String.format(
                "Defects size: %d does not match with expected: %d, (one object name is already reserved)",
                found.size(), expected
            ),
            found,
            Matchers.hasSize(expected)
        );
    }

    @Test
    void reportsMultipleNames() throws IOException {
        MatcherAssert.assertThat(
            "Defects size does not match with expected",
            new LtReservedName(
                new MapOf<String, String>(
                    new MapEntry<>("ja", "org.eolang.ja.eo"),
                    new MapEntry<>("spb", "org.eolang.spb.eo")
                )
            ).defects(
                new EoSyntax(
                    String.join(
                        "\n",
                        "# JA.",
                        "[] > ja",
                        "  52 > spb"
                    )
                ).parsed()
            ),
            Matchers.hasSize(2)
        );
    }

    @Test
    void reportsReservedNameInTopObject() throws IOException {
        MatcherAssert.assertThat(
            "Defects size does not match with expected",
            new LtReservedName(
                new MapOf<>("foo", "org.eolang.foo.eo")
            ).defects(
                new EoSyntax(
                    String.join(
                        "\n",
                        "# Foo.",
                        "[] > foo",
                        "  52 > spb"
                    )
                ).parsed()
            ),
            Matchers.hasSize(1)
        );
    }

    @Test
    void reportsCorrectMessageForReservedNameInTopObject() throws IOException {
        MatcherAssert.assertThat(
            "The name of high-level object 'foo' should be reported",
            new ListOf<>(
                new LtReservedName(
                    new MapOf<>("foo", "org.eolang.foo.eo")
                ).defects(
                    new EoSyntax(
                        String.join(
                            "\n",
                            "# Foo.",
                            "[] > foo",
                            "  52 > spb"
                        )
                    ).parsed()
                )
            ).get(0).text(),
            Matchers.equalTo(
                "Object name \"foo\" is already reserved by object in the \"org.eolang.foo.eo\""
            )
        );
    }

    @Tag("reserved")
    @Test
    void scansReservedFromHome() throws Exception {
        MatcherAssert.assertThat(
            "Defects size does not match with expected",
            new LtReservedName().defects(
                new EoSyntax(
                    String.join(
                        "\n",
                        "# Bar.",
                        "[] > bar",
                        "  52 > stdout"
                    )
                ).parsed()
            ),
            Matchers.hasSize(1)
        );
    }

    @Tag("reserved")
    @Test
    void scansReservedFromHomeWithCorrectMessage() throws Exception {
        MatcherAssert.assertThat(
            "Defect message does not match with expected",
            new ListOf<>(
                new LtReservedName().defects(
                    new EoSyntax(
                        String.join(
                            "\n",
                            "# Baz.",
                            "[] > baz",
                            "  52 > stdout"
                        )
                    ).parsed()
                )
            ).get(0).text(),
            Matchers.equalTo(
                "Object name \"stdout\" is already reserved by object in the \"org.eolang.io.stdout.eo\""
            )
        );
    }

    @Test
    void allowsAllUnique() throws IOException {
        MatcherAssert.assertThat(
            "Object names should not be reported, since they all unique",
            new LtReservedName(new MapOf<>()).defects(new EoSyntax("[] > qux").parsed()),
            Matchers.emptyIterable()
        );
    }
}
