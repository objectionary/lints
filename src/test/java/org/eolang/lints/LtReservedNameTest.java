/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import org.cactoos.list.ListOf;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.eolang.parser.EoSyntax;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

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
                            "# Foo.",
                            "[] > foo",
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
        final String target = "foo";
        final Collection<Defect> defects = new LtReservedName(
            new MapOf<>("foo", "org.eolang.foo.eo")
        ).defects(
            new EoSyntax(
                String.join(
                    "\n",
                    "# Foo.",
                    String.format("[] > %s", target),
                    "  52 > spb"
                )
            ).parsed()
        );
        MatcherAssert.assertThat(
            "Defects size does not match with expected",
            defects,
            Matchers.hasSize(1)
        );
        MatcherAssert.assertThat(
            String.format(
                "The name of high-level object \"%s\" should be reported",
                target
            ),
            new ListOf<>(defects).get(0).text(),
            Matchers.equalTo(
                "Object name \"foo\" is already reserved by object in the \"org.eolang.foo.eo\""
            )
        );
    }

    @Tag("deep")
    @Timeout(value = 90L, unit = TimeUnit.SECONDS)
    @Test
    void scansReservedFromHome() throws IOException {
        final Lint<XML> lint = new LtReservedName();
        final Collection<Defect> defects = lint.defects(
            new EoSyntax(
                String.join(
                    "\n",
                    "# Foo",
                    "[] > foo",
                    "  52 > stdout"
                )
            ).parsed()
        );
        MatcherAssert.assertThat(
            "Defects size does not match with expected",
            defects,
            Matchers.hasSize(1)
        );
        MatcherAssert.assertThat(
            "Defect message does not match with expected",
            new ListOf<>(defects).get(0).text(),
            Matchers.equalTo(
                "Object name \"stdout\" is already reserved by object in the \"org.eolang.io.stdout.eo\""
            )
        );
    }
}
