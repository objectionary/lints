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
 * Tests for {@link LtInconsistentArgs}.
 *
 * @since 0.0.41
 */
final class LtInconsistentArgsTest {

    @Test
    void catchesArgumentsInconsistency() throws IOException {
        MatcherAssert.assertThat(
            "Defects are empty, but they should not",
            new LtInconsistentArgs().defects(
                new MapOf<>(
                    new MapEntry<>(
                        "foo",
                        new EoSyntax(
                            String.join(
                                "\n",
                                "# Foo",
                                "[] > foo",
                                "  bar 42 > x",
                                "  bar 1 2 3 > y"
                            )
                        ).parsed()
                    )
                )
            ),
            Matchers.hasSize(2)
        );
    }

    @Test
    void allowsConsistentArgumentsPassing() throws IOException {
        MatcherAssert.assertThat(
            "Defects are not empty, but they should",
            new LtInconsistentArgs().defects(
                new MapOf<>(
                    new MapEntry<>(
                        "app",
                        new EoSyntax(
                            String.join(
                                "\n",
                                "# This is app",
                                "[] > app",
                                "  foo 42 > x",
                                "  foo 52 > spb"
                            )
                        ).parsed()
                    )
                )
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void catchesInconsistencyAcrossPrograms() throws IOException {
        MatcherAssert.assertThat(
            "Defects are not caught across multiple programs, but they should",
            new LtInconsistentArgs().defects(
                new MapOf<String, XML>(
                    new MapEntry<>(
                        "app",
                        new EoSyntax(
                            "f-one-arg",
                            String.join(
                                "\n",
                                "# App",
                                "[] > app",
                                "  f 42 > x"
                            )
                        ).parsed()
                    ),
                    new MapEntry<>(
                        "main",
                        new EoSyntax(
                            "f-three-arg",
                            String.join(
                                "\n",
                                "# Main",
                                "[] > main",
                                "  f 1 2 3 > y"
                            )
                        ).parsed()
                    )
                )
            ),
            Matchers.hasSize(2)
        );
    }

    @Test
    void allowsConsistentArgumentsAcrossPrograms() throws IOException {
        MatcherAssert.assertThat(
            "Defects are not empty, but they should",
            new LtInconsistentArgs().defects(
                new MapOf<String, XML>(
                    new MapEntry<>(
                        "fizz",
                        new EoSyntax(
                            String.join(
                                "\n",
                                "# Fizz",
                                "[] > fizz",
                                "  f 42 > x"
                            )
                        ).parsed()
                    ),
                    new MapEntry<>(
                        "buzz",
                        new EoSyntax(
                            String.join(
                                "\n",
                                "# Buzz",
                                "[a] > main",
                                "  f a > x"
                            )
                        ).parsed()
                    )
                )
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void catchesInconsistencyAcrossProgramsWithAlias() throws IOException {
        MatcherAssert.assertThat(
            "Defects are not caught across multiple programs with alias, but they should",
            new LtInconsistentArgs().defects(
                new MapOf<String, XML>(
                    new MapEntry<>(
                        "text-fqn",
                        new EoSyntax(
                            "text-fqn",
                            String.join(
                                "\n",
                                "# App",
                                "[] > app",
                                "  Q.org.eolang.txt.text \"f\" \"y\" > x"
                            )
                        ).parsed()
                    ),
                    new MapEntry<>(
                        "text-alias",
                        new EoSyntax(
                            "text-alias",
                            String.join(
                                "\n",
                                "+alias org.eolang.txt.text",
                                "# Main",
                                "[] > main",
                                "  text \"f\" > y"
                            )
                        ).parsed()
                    )
                )
            ),
            Matchers.hasSize(2)
        );
    }
}
