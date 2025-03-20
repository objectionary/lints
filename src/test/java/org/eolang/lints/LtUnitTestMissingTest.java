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
                    new MapEntry<>("bar", new XMLDocument("<program name='bar'/>")),
                    new MapEntry<>("bar-tests", new XMLDocument("<program name='bar-tests'/>"))
                )
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void acceptsValidDirectory(@Mktmp final Path dir) throws IOException {
        Files.write(dir.resolve("foo.xmir"), "<program name='foo'/>".getBytes());
        Files.write(dir.resolve("foo-tests.xmir"), "<program name='foo-tests'/>".getBytes());
        MatcherAssert.assertThat(
            "some defects found by mistake",
            new Programs(dir).defects(),
            Matchers.emptyIterable()
        );
    }

    @SuppressWarnings("JTCOP.RuleNotContainsTestWord")
    @Test
    void issuesDetectsOnMissingTest(@Mktmp final Path dir) throws IOException {
        Files.write(dir.resolve("aaa.xmir"), "<program name='aaa'/>".getBytes());
        MatcherAssert.assertThat(
            " defects found",
            new Programs(dir).defects(),
            Matchers.allOf(
                Matchers.<Defect>iterableWithSize(Matchers.greaterThan(0)),
                Matchers.<Defect>everyItem(new DefectMatcher())
            )
        );
    }
}
