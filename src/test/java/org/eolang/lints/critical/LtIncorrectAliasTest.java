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
package org.eolang.lints.critical;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import com.yegor256.Mktmp;
import com.yegor256.MktmpResolver;
import java.nio.file.Files;
import java.nio.file.Path;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.eolang.lints.ParsedEo;
import org.eolang.lints.Programs;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests for {@link LtIncorrectAlias}.
 *
 * @since 0.0.30
 */
final class LtIncorrectAliasTest {

    @Test
    void catchesBrokenAlias() throws Exception {
        MatcherAssert.assertThat(
            "Defects are empty, but shouldn't be",
            new LtIncorrectAlias().defects(
                new MapOf<>(
                    new MapEntry<>(
                        "bar",
                        new ParsedEo(
                            "org/eolang/lints/critical/incorrect-alias/bar.eo"
                        ).value()
                    )
                )
            ),
            Matchers.hasSize(Matchers.greaterThan(0))
        );
    }

    @Test
    void passesIfFileExists() throws Exception {
        MatcherAssert.assertThat(
            "Defects aren't empty, but should be",
            new LtIncorrectAlias().defects(
                new MapOf<String, XML>(
                    new MapEntry<>(
                        "bar",
                        new ParsedEo(
                            "org/eolang/lints/critical/incorrect-alias/bar.eo"
                        ).value()
                    ),
                    new MapEntry<>("ttt/foo", new XMLDocument("<program/>"))
                )
            ),
            Matchers.hasSize(0)
        );
    }

    @ParameterizedTest
    @ValueSource(
        strings = {
            "org/eolang/lints/critical/incorrect-alias/no-aliases.eo",
            "org/eolang/lints/critical/incorrect-alias/no-package.eo"
        }
    )
    void ignoresProgram(final String path) throws Exception {
        MatcherAssert.assertThat(
            "Defects aren't empty, but should be",
            new LtIncorrectAlias().defects(
                new MapOf<>(
                    new MapEntry<>(
                        "foo",
                        new ParsedEo(path).value()
                    )
                )
            ),
            Matchers.hasSize(0)
        );
    }

    @Test
    void scansSecondPartInLongerAlias() throws Exception {
        MatcherAssert.assertThat(
            "Defects aren't empty, but they should",
            new LtIncorrectAlias().defects(
                new MapOf<String, XML>(
                    new MapEntry<>(
                        "longer-alias",
                        new ParsedEo(
                            "org/eolang/lints/critical/incorrect-alias/longer-alias.eo"
                        ).value()
                    ),
                    new MapEntry<>(
                        "org/eolang/io/stdout", new XMLDocument("<program><objects/></program>")
                    )
                )
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    @ExtendWith(MktmpResolver.class)
    void acceptsValidDirectory(@Mktmp final Path dir) throws Exception {
        Files.write(
            dir.resolve("bar.xmir"),
            new ParsedEo("org/eolang/lints/critical/incorrect-alias/bar.eo")
                .value().toString().getBytes()
        );
        Files.createDirectory(dir.resolve("ttt"));
        Files.write(dir.resolve("ttt/foo.xmir"), "<program/>".getBytes());
        Files.write(dir.resolve("bar-test.xmir"), "<program/>".getBytes());
        Files.write(dir.resolve("ttt/foo-test.xmir"), "<program/>".getBytes());
        MatcherAssert.assertThat(
            "Defects are not empty, but should be",
            new Programs(dir).defects(),
            Matchers.emptyIterable()
        );
    }

    @Test
    @ExtendWith(MktmpResolver.class)
    void acceptsValidDirectoryWithLongerAlias(@Mktmp final Path dir) throws Exception {
        Files.write(
            dir.resolve("main.xmir"),
            new ParsedEo("org/eolang/lints/critical/incorrect-alias/longer-alias.eo")
                .value().toString().getBytes()
        );
        Files.write(dir.resolve("main-test.xmir"), "<program><objects/></program>".getBytes());
        Files.createDirectory(
            Files.createDirectory(
                Files.createDirectory(
                    dir.resolve("org")
                ).resolve("eolang")
            ).resolve("io")
        );
        Files.write(
            dir.resolve("org/eolang/io/stdout.xmir"), "<program><objects/></program>".getBytes()
        );
        Files.write(
            dir.resolve("org/eolang/io/stdout-test.xmir"),
            "<program><objects/></program>".getBytes()
        );
        MatcherAssert.assertThat(
            "Defects are not empty, but should be",
            new Programs(dir).defects(),
            Matchers.emptyIterable()
        );
    }
}
