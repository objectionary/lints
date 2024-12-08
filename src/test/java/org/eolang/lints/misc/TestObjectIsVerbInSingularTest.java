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

import com.yegor256.MayBeSlow;
import com.yegor256.WeAreOnline;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import org.cactoos.io.InputOf;
import org.eolang.jucs.ClasspathSource;
import org.eolang.parser.EoSyntax;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * Tests for {@link TestObjectIsVerbInSingular}.
 *
 * @since 0.0.20
 * @todo #72:90min Cache POS model after it's first load.
 *  Currently, we load the model from the internet for each test run. Instead of
 *  this, let's cache the model after the first load in both: filesystem, and
 *  RAM. This optimization should improve local testing experience. This was
 *  already implemented <a href="https://github.com/volodya-lombrozo/jtcop/blob/main/src/main/java/com/github/lombrozo/testnames/rules/ml/CachedModelSource.java">here</a>.
 */
final class TestObjectIsVerbInSingularTest {

    /**
     * Model.
     */
    private static POSTaggerME model;

    @BeforeAll
    static void setUp() throws IOException, URISyntaxException {
        TestObjectIsVerbInSingularTest.model = new POSTaggerME(
            new POSModel(
                new URI(
                    "https://opennlp.sourceforge.net/models-1.5/en-pos-perceptron.bin"
                ).toURL()
            )
        );
    }

    @ExtendWith(MayBeSlow.class)
    @ExtendWith(WeAreOnline.class)
    @ParameterizedTest
    @ClasspathSource(
        value = "org/eolang/lints/misc/test-object-is-not-verb-in-singular/bad",
        glob = "**.eo"
    )
    void catchesBadName(final String source) throws IOException {
        MatcherAssert.assertThat(
            "Defects shouldn't be empty",
            new TestObjectIsVerbInSingular(TestObjectIsVerbInSingularTest.model).defects(
                new EoSyntax(
                    new InputOf(source)
                ).parsed()
            ),
            Matchers.hasSize(Matchers.greaterThan(0))
        );
    }

    @ParameterizedTest
    @ClasspathSource(
        value = "org/eolang/lints/misc/test-object-is-not-verb-in-singular/good",
        glob = "**.eo"
    )
    void allowsGoodNames(final String source) throws IOException {
        MatcherAssert.assertThat(
            "Defects are not empty, but they shouldn't be",
            new TestObjectIsVerbInSingular(TestObjectIsVerbInSingularTest.model).defects(
                new EoSyntax(
                    new InputOf(source)
                ).parsed()
            ),
            Matchers.hasSize(0)
        );
    }
}
