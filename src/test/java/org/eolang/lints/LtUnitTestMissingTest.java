/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import com.yegor256.Mktmp;
import com.yegor256.MktmpResolver;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import matchers.DefectMatcher;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test for {@link LtUnitTestMissing}.
 *
 * @since 0.0.1
 */
@ExtendWith(MktmpResolver.class)
final class LtUnitTestMissingTest {

    @Test
    void acceptsValidPackage() throws IOException {
        MatcherAssert.assertThat(
            "some problems found by mistake",
            new LtUnitTestMissing().defects(
                new MapOf<String, XML>(
                    new MapEntry<>("bar", new XMLDocument("<object><o name='bar'/></object>")),
                    new MapEntry<>(
                        "bar-tests", new XMLDocument("<object><o name='bar-tests'/></object>")
                    )
                )
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void acceptsValidDirectory(@Mktmp final Path dir) throws IOException {
        Files.write(
            dir.resolve("foo.xmir"),
            "<object><o name='foo'/></object>".getBytes(StandardCharsets.UTF_8)
        );
        Files.write(
            dir.resolve("foo-tests.xmir"),
            "<object><o name='foo-tests'/></object>".getBytes(StandardCharsets.UTF_8)
        );
        MatcherAssert.assertThat(
            "some defects found by mistake",
            new Program(dir).defects(),
            Matchers.emptyIterable()
        );
    }

    @SuppressWarnings("JTCOP.RuleNotContainsTestWord")
    @Test
    void issuesDetectsOnMissingTest(@Mktmp final Path dir) throws IOException {
        Files.write(
            dir.resolve("aaa.xmir"),
            "<object><o name='aaa'/></object>".getBytes(StandardCharsets.UTF_8)
        );
        MatcherAssert.assertThat(
            " defects found",
            new Program(dir).defects(),
            Matchers.allOf(
                Matchers.<Defect>iterableWithSize(Matchers.greaterThan(0)),
                Matchers.<Defect>everyItem(new DefectMatcher())
            )
        );
    }
}
