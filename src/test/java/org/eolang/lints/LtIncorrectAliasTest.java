/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import com.yegor256.Mktmp;
import com.yegor256.MktmpResolver;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import matchers.DefectMatcher;
import org.cactoos.io.ResourceOf;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.eolang.parser.EoSyntax;
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
                        new EoSyntax(
                            String.join(
                                "\n",
                                "+alias foo",
                                "+package ttt\n",
                                "# Bar",
                                "[] > bar",
                                "  foo > @"
                            )
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
    void passesIfFileExists() throws Exception {
        MatcherAssert.assertThat(
            "Defects aren't empty, but should be",
            new LtIncorrectAlias().defects(
                new MapOf<String, XML>(
                    new MapEntry<>(
                        "bar",
                        new EoSyntax(
                            String.join(
                                "\n",
                                "+alias foo",
                                "+package ttt\n",
                                "# Bar",
                                "[] > bar",
                                "  foo > @"
                            )
                        ).parsed()
                    ),
                    new MapEntry<>("foo", new XMLDocument("<object><o name='foo'/></object>"))
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
    void ignoresSource(final String path) throws Exception {
        MatcherAssert.assertThat(
            "Defects aren't empty, but should be",
            new LtIncorrectAlias().defects(
                new MapOf<>(new MapEntry<>("foo", new EoSyntax(new ResourceOf(path)).parsed()))
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
                        new EoSyntax(
                            String.join(
                                "\n",
                                "+alias stdout org.eolang.io.stdout",
                                "+package foo\n",
                                "[] > main",
                                "  stdout > @",
                                "    \"hi!\""
                            )
                        ).parsed()
                    ),
                    new MapEntry<>(
                        "org.eolang.io.stdout",
                        new XMLDocument("<object><o name='stdout'/></object>")
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
            new EoSyntax(
                String.join(
                    "\n",
                    "+alias ttt.foo",
                    "+package ttt\n",
                    "# Bar",
                    "[] > bar",
                    "  foo > @"
                )
            ).parsed().toString().getBytes(StandardCharsets.UTF_8)
        );
        Files.createDirectory(dir.resolve("ttt"));
        final String source = "<object/>";
        Files.write(dir.resolve("ttt/foo.xmir"), source.getBytes(StandardCharsets.UTF_8));
        Files.write(dir.resolve("bar-tests.xmir"), source.getBytes(StandardCharsets.UTF_8));
        Files.write(
            dir.resolve("ttt/foo-tests.xmir"),
            source.getBytes(StandardCharsets.UTF_8)
        );
        MatcherAssert.assertThat(
            "Defects are not empty, but should be",
            new Program(dir).defects(),
            Matchers.emptyIterable()
        );
    }

    @Test
    @ExtendWith(MktmpResolver.class)
    void acceptsValidDirectoryWithLongerAlias(@Mktmp final Path dir) throws Exception {
        Files.write(
            dir.resolve("main.xmir"),
            new EoSyntax(
                String.join(
                    "\n",
                    "+alias stdout org.eolang.io.stdout",
                    "+package foo\n",
                    "[] > main",
                    "  stdout > @",
                    "    \"hi!\""
                )
            ).parsed().toString().getBytes(StandardCharsets.UTF_8)
        );
        final String source = "<object/>";
        Files.write(
            dir.resolve("main-tests.xmir"),
            source.getBytes(StandardCharsets.UTF_8)
        );
        Files.createDirectory(
            Files.createDirectory(
                Files.createDirectory(
                    dir.resolve("org")
                ).resolve("eolang")
            ).resolve("io")
        );
        Files.write(
            dir.resolve("org/eolang/io/stdout.xmir"),
            source.getBytes(StandardCharsets.UTF_8)
        );
        Files.write(
            dir.resolve("org/eolang/io/stdout-tests.xmir"),
            source.getBytes(StandardCharsets.UTF_8)
        );
        MatcherAssert.assertThat(
            "Defects are not empty, but should be",
            new Program(dir).defects(),
            Matchers.emptyIterable()
        );
    }
}
