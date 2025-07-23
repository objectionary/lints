/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.io.IOException;
import org.cactoos.list.ListOf;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.eolang.parser.EoSyntax;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link LtUnlintNonExistingDefectWpa}.
 *
 * @since 0.0.42
 */
final class LtUnlintNonExistingDefectWpaTest {

    @Test
    void allowsUnlintingExistingWpaDefects() throws IOException {
        MatcherAssert.assertThat(
            "Lint should not complain, since program has WPA defects",
            new LtUnlintNonExistingDefectWpa(
                new ListOf<>(new LtObjectIsNotUnique()),
                new ListOf<>()
            ).defects(
                new MapOf<String, XML>(
                    new MapEntry<>(
                        "foo",
                        new EoSyntax(
                            String.join(
                                "\n",
                                "+unlint object-is-not-unique",
                                "",
                                "# Foo.",
                                "[] > foo"
                            )
                        ).parsed()
                    ),
                    new MapEntry<>(
                        "bar",
                        new EoSyntax(
                            String.join(
                                "\n",
                                "+unlint object-is-not-unique",
                                "",
                                "# Bar.",
                                "[] > foo"
                            )
                        ).parsed()
                    )
                )
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void catchesUnlintOfNonExistingWpaDefects() throws IOException {
        MatcherAssert.assertThat(
            "Defects are not empty, but they should be, since +unlint unlints non-existing defect",
            new LtUnlintNonExistingDefectWpa(
                new ListOf<>(new LtObjectIsNotUnique()),
                new ListOf<>()
            ).defects(
                new MapOf<>(
                    "bar",
                    new EoSyntax(
                        String.join(
                            "\n",
                            "+unlint object-is-not-unique",
                            "",
                            "# Bar",
                            "[] > bar"
                        )
                    ).parsed()
                )
            ),
            Matchers.hasSize(Matchers.greaterThan(0))
        );
    }

    @Test
    void allowsNoUnlints() {
        MatcherAssert.assertThat(
            "Defects are not empty, but they should",
            new LtUnlintNonExistingDefectWpa(
                new ListOf<>(new LtObjectIsNotUnique()),
                new ListOf<>()
            ).defects(
                new MapOf<String, XML>(
                    new MapEntry<>(
                        "f",
                        new XMLDocument(
                            String.join(
                                "\n",
                                "<object>",
                                "  <o name='f'/>",
                                "</object>"
                            )
                        )
                    ),
                    new MapEntry<>(
                        "fa",
                        new XMLDocument(
                            String.join(
                                "\n",
                                "<object>",
                                "  <o name='fa'/>",
                                "</object>"
                            )
                        )
                    )
                )
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void reportsWithWpaSupplied() throws IOException {
        MatcherAssert.assertThat(
            "Defects are empty, but they should not",
            new LtUnlintNonExistingDefectWpa(
                new PkWpa(),
                new ListOf<>()
            ).defects(
                new MapOf<String, XML>(
                    new MapEntry<>(
                        "f",
                        new EoSyntax(
                            String.join(
                                "\n",
                                "+unlint unit-test-without-live-file",
                                "",
                                "# E tests.",
                                "[] > f"
                            )
                        ).parsed()
                    ),
                    new MapEntry<>("e", new XMLDocument("<object><o name='e'/></object>"))
                )
            ),
            Matchers.hasSize(Matchers.greaterThan(0))
        );
    }

    @Test
    void ignoresSingleSourceUnlint() throws IOException {
        MatcherAssert.assertThat(
            "Single source unlint is not ignored, but it should be",
            new LtUnlintNonExistingDefectWpa(
                new ListOf<>(new LtAtomIsNotUnique()),
                new ListOf<>(new MonoLintNames())
            ).defects(
                new MapOf<>(
                    new MapEntry<>(
                        "",
                        new EoSyntax(
                            String.join(
                                "\n",
                                "+unlint mandatory-home",
                                "",
                                "# Boom",
                                "[] > boom"
                            )
                        ).parsed()
                    )
                )
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void allowsExistingUnlintWithLineNumber() throws IOException {
        MatcherAssert.assertThat(
            "An existing defect should be able to be unlinted with line number",
            new LtUnlintNonExistingDefectWpa(
                new ListOf<>(new LtInconsistentArgs()),
                new ListOf<>()
            ).defects(
                new MapOf<>(
                    new MapEntry<>(
                        "foo",
                        new EoSyntax(
                            String.join(
                                "\n",
                                "+unlint inconsistent-args:6",
                                "",
                                "# Foo.",
                                "[] > foo",
                                "  bar 42 > x",
                                "  bar 42 52 > y"
                            )
                        ).parsed()
                    )
                )
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void catchesNonExistingUnlintWithLineNumber() throws IOException {
        MatcherAssert.assertThat(
            "Non existing defect with line number should be reported",
            new LtUnlintNonExistingDefectWpa(
                new ListOf<>(new LtInconsistentArgs()),
                new ListOf<>()
            ).defects(
                new MapOf<>(
                    new MapEntry<>(
                        "app",
                        new EoSyntax(
                            String.join(
                                "\n",
                                "+unlint inconsistent-args:25",
                                "",
                                "# App.",
                                "[] > app",
                                "  tee 42 > x",
                                "  tee 42 52 > y"
                            )
                        ).parsed()
                    )
                )
            ),
            Matchers.hasSize(Matchers.greaterThan(0))
        );
    }

    @Test
    void allowsUnlintForDefectsInTheLineRange() throws IOException {
        MatcherAssert.assertThat(
            "Defects are not empty, but they should",
            new LtUnlintNonExistingDefectWpa(
                new ListOf<>(new LtInconsistentArgs()),
                new ListOf<>()
            ).defects(
                new MapOf<>(
                    "main",
                    new EoSyntax(
                        String.join(
                            "\n",
                            "+unlint inconsistent-args:5-7",
                            "",
                            "# Main.",
                            "[] > main",
                            "  fork > parent",
                            "  fork parent > child",
                            "  fork child parent > subchild"
                        )
                    ).parsed()
                )
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void catchesUnlintWithOutOfRangeLines() throws IOException {
        MatcherAssert.assertThat(
            "Defects are empty, but they should not",
            new LtUnlintNonExistingDefectWpa(
                new ListOf<>(new LtInconsistentArgs()),
                new ListOf<>()
            ).defects(
                new MapOf<>(
                    "main",
                    new EoSyntax(
                        String.join(
                            "\n",
                            "+unlint inconsistent-args:12-44",
                            "",
                            "# Semaphore.",
                            "[] > sem",
                            "  p 1 > lock-one",
                            "  p 1 1 > lock-more"
                        )
                    ).parsed()
                )
            ),
            Matchers.iterableWithSize(1)
        );
    }

    @Test
    void catchesUnlintWithSomeLineOutOfRange() throws IOException {
        MatcherAssert.assertThat(
            "Defects are empty, but they should not",
            new LtUnlintNonExistingDefectWpa(
                new ListOf<>(new LtInconsistentArgs()),
                new ListOf<>()
            ).defects(
                new MapOf<>(
                    "main",
                    new EoSyntax(
                        String.join(
                            "\n",
                            "+unlint inconsistent-args:1-5",
                            "",
                            "# Object that represents Jeffrey from a real world.",
                            "[] > jeff",
                            "  send 0 > content",
                            "  send content \"boss@google.com\" > emailed",
                            "  send emailed > @"
                        )
                    ).parsed()
                )
            ),
            Matchers.iterableWithSize(1)
        );
    }
}
