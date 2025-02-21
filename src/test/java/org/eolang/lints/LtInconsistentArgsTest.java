/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2025 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
}
