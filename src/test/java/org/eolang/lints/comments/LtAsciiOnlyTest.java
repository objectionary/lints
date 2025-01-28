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
package org.eolang.lints.comments;

import java.io.IOException;
import matchers.DefectMatcher;
import org.cactoos.list.ListOf;
import org.eolang.lints.Defect;
import org.eolang.parser.EoSyntax;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link LtAsciiOnly}.
 *
 * @since 0.0.1
 */
final class LtAsciiOnlyTest {

    @Test
    void catchesSomeNonAsciiInComment() throws IOException {
        MatcherAssert.assertThat(
            "non-ascii comment is not welcome",
            new LtAsciiOnly().defects(
                new EoSyntax(
                    "# привет\n# как дела?\n[] > foo\n"
                ).parsed()
            ),
            Matchers.allOf(
                Matchers.<Defect>iterableWithSize(Matchers.greaterThan(0)),
                Matchers.<Defect>everyItem(new DefectMatcher())
            )
        );
    }

    @Test
    void catchesNonAsciiInComment() throws IOException {
        MatcherAssert.assertThat(
            "non-ascii comment error should contain abusive character",
            new ListOf<>(
                new LtAsciiOnly().defects(
                    new EoSyntax(
                        "# привет\n# как дела?\n[] > foo\n"
                    ).parsed()
                )
            ).get(0).text(),
            Matchers.containsString("Only ASCII characters are allowed in comments")
        );
    }

    @Test
    void explainsMotive() throws Exception {
        MatcherAssert.assertThat(
            "The motive doesn't contain expected string",
            new LtAsciiOnly().motive().contains("# ASCII-Only Characters in Comment"),
            new IsEqual<>(true)
        );
    }

    @Test
    void setsRuleCorrectly() throws Exception {
        MatcherAssert.assertThat(
            "The rule name is set right",
            new LtAsciiOnly().defects(
                new EoSyntax(
                    "# тук тук\n[] > foo\n"
                ).parsed()
            ).iterator().next().rule(),
            Matchers.equalTo("ascii-only")
        );
    }

}
