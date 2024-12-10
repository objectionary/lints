/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2024 Objectionary.com
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
package org.eolang.lints.misc;

import com.jcabi.xml.XML;
import com.yegor256.MayBeSlow;
import com.yegor256.WeAreOnline;
import java.io.IOException;
import org.cactoos.io.ResourceOf;
import org.eolang.lints.Lint;
import org.eolang.parser.EoSyntax;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Tests for {@link TestObjectIsVerbInSingular}.
 *
 * @since 0.0.20
 */
final class TestObjectIsVerbInSingularTest {

    /**
     * Lint.
     */
    private static Lint<XML> lint;

    @BeforeAll
    static void setUp() throws Exception {
        TestObjectIsVerbInSingularTest.lint = new TestObjectIsVerbInSingular();
    }

    @ExtendWith(WeAreOnline.class)
    @ExtendWith(MayBeSlow.class)
    @Test
    void catchesBadName() throws Exception {
        MatcherAssert.assertThat(
            "Defects size doesn't match with expected",
            TestObjectIsVerbInSingularTest.lint.defects(
                new EoSyntax(
                    new ResourceOf(
                        "org/eolang/lints/misc/test-object-is-not-verb-in-singular/bad-tests.eo"
                    )
                ).parsed()
            ),
            Matchers.hasSize(38)
        );
    }

    @ExtendWith(WeAreOnline.class)
    @ExtendWith(MayBeSlow.class)
    @Test
    void allowsGoodNames() throws IOException {
        MatcherAssert.assertThat(
            "Defects are not empty, but they shouldn't be",
            TestObjectIsVerbInSingularTest.lint.defects(
                new EoSyntax(
                    new ResourceOf(
                        "org/eolang/lints/misc/test-object-is-not-verb-in-singular/good-tests.eo"
                    )
                ).parsed()
            ),
            Matchers.hasSize(0)
        );
    }
}
