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
import com.jcabi.xml.XMLDocument;
import io.github.secretx33.resourceresolver.PathMatchingResourcePatternResolver;
import io.github.secretx33.resourceresolver.Resource;
import java.io.IOException;
import java.util.Arrays;
import org.cactoos.io.InputOf;
import org.cactoos.proc.ForEach;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link PkByXsl}.
 *
 * @since 0.0.1
 */
final class PkByXslTest {

    @Test
    void passesOnSimpleXmir() throws IOException {
        final XML xmir = new XMLDocument("<program name='no-exceptions'/>");
        for (final Lint<XML> lint : new PkByXsl()) {
            MatcherAssert.assertThat(
                "passes with no exceptions",
                lint.defects(xmir),
                Matchers.notNullValue()
            );
        }
    }

    @Test
    void checksIdsOfEveryXslStylesheet() throws Exception {
        new ForEach<Resource>(
            res -> {
                final XML sheet = new XMLDocument(
                    new TextOf(new InputOf(res.getInputStream())).asString()
                );
                MatcherAssert.assertThat(
                    "the @id is equal to the basename of the file",
                    sheet.xpath("/xsl:stylesheet/@id").get(0),
                    Matchers.equalTo(res.getFilename().replaceAll(".xsl$", ""))
                );
            }
        ).exec(
            Arrays.asList(
                new PathMatchingResourcePatternResolver().getResources(
                    "classpath*:org/eolang/lints/**/*.xsl"
                )
            )
        );
    }

    @Test
    void checksAllMotives() throws Exception {
        for (final Lint<XML> lint : new PkByXsl()) {
            MatcherAssert.assertThat(
                "Lint's motive is empty, but should not be",
                lint.motive().isEmpty(),
                Matchers.equalTo(false)
            );
        }
    }

}
