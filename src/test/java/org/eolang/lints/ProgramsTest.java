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

import com.yegor256.Mktmp;
import com.yegor256.MktmpResolver;
import com.yegor256.Together;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import matchers.DefectMatcher;
import org.cactoos.list.ListOf;
import org.cactoos.set.SetOf;
import org.eolang.parser.EoSyntax;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test for {@link Programs}.
 *
 * @since 0.1.0
 */
@ExtendWith(MktmpResolver.class)
final class ProgramsTest {

    @Test
    void simpleTest(@Mktmp final Path dir) throws IOException {
        MatcherAssert.assertThat(
            "the defect is found",
            new Programs(
                this.withProgram(
                    dir,
                    "a/b/c/foo.xmir",
                    "# first.\n[] > foo\n# second.\n[] > foo\n"
                )
            ).defects(),
            Matchers.allOf(
                Matchers.<Defect>iterableWithSize(Matchers.greaterThan(0)),
                Matchers.<Defect>everyItem(new DefectMatcher())
            )
        );
    }

    @Test
    void skipsAllWarnings(@Mktmp final Path dir) throws IOException {
        MatcherAssert.assertThat(
            "the defect is found",
            new Programs(
                this.withProgram(
                    dir,
                    "bar.xmir",
                    String.join(
                        "\n",
                        "+unlint unit-test-missing",
                        "# Test.",
                        "[] > foo"
                    )
                )
            ).defects(),
            Matchers.emptyIterable()
        );
    }

    @RepeatedTest(5)
    void checksInParallel(@Mktmp final Path dir) throws IOException {
        final Path program = this.withProgram(
            dir,
            "foo.xmir",
            "# first.\n# second.\n[] > foo\n"
        );
        MatcherAssert.assertThat(
            "",
            new SetOf<>(
                new Together<>(
                    thread -> new Programs(program).defects().size()
                )
            ).size(),
            Matchers.equalTo(1)
        );
    }

    @Test
    void doesNotThrowIoException() {
        Assertions.assertDoesNotThrow(
            () -> new Programs(new ListOf<>()).defects(),
            "Exception was thrown, but it should not be"
        );
    }

    private Path withProgram(final Path dir, final String name,
        final String text) throws IOException {
        final Path path = dir.resolve(name);
        path.toFile().getParentFile().mkdirs();
        Files.write(
            path,
            new EoSyntax(
                text
            ).parsed().toString().getBytes(StandardCharsets.UTF_8)
        );
        return dir;
    }

}
