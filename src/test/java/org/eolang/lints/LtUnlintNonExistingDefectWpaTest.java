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
                new ListOf<>(new LtUnitTestMissing()),
                new ListOf<>()
            ).defects(
                new MapOf<>(
                    "foo",
                    new EoSyntax(
                        String.join(
                            "\n",
                            "+unlint unit-test-missing",
                            "",
                            "# Foo",
                            "[] > foo"
                        )
                    ).parsed()
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
                new ListOf<>(new LtUnitTestMissing()),
                new ListOf<>()
            ).defects(
                new MapOf<String, XML>(
                    new MapEntry<>(
                        "bar",
                        new EoSyntax(
                            String.join(
                                "\n",
                                "+unlint unit-test-missing",
                                "",
                                "# Bar",
                                "[] > bar"
                            )
                        ).parsed()
                    ),
                    new MapEntry<>("bar-tests", new XMLDocument("<object/>"))
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
                new ListOf<>(new LtUnitTestMissing()),
                new ListOf<>()
            ).defects(
                new MapOf<String, XML>(
                    new MapEntry<>("f", new XMLDocument("<object/>")),
                    new MapEntry<>("f-tests", new XMLDocument("<object/>"))
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
                new ListOf<>(new LtUnitTestMissing()),
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
}
