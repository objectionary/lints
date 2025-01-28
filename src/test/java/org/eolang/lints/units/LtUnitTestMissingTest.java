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
package org.eolang.lints.units;

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
import org.eolang.lints.Defect;
import org.eolang.lints.Programs;
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

    @Test
    void detectsMissingTest(@Mktmp final Path dir) throws IOException {
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
