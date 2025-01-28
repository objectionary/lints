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
import io.github.secretx33.resourceresolver.PathMatchingResourcePatternResolver;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Pattern;
import org.cactoos.io.InputOf;
import org.cactoos.iterable.IterableEnvelope;
import org.cactoos.iterable.Mapped;
import org.cactoos.iterable.Shuffled;

/**
 * All lints defined by XSLs.
 *
 * @since 0.1.0
 */
final class PkByXsl extends IterableEnvelope<Lint<XML>> {

    /**
     * XSL extension pattern.
     */
    private static final Pattern XSL_PATTERN = Pattern.compile(
        ".xsl", Pattern.LITERAL
    );

    /**
     * Lint paths pattern.
     */
    private static final Pattern LINTS_PATH = Pattern.compile(
        "eolang/lints", Pattern.LITERAL
    );

    /**
     * Ctor.
     */
    PkByXsl() {
        super(PkByXsl.all());
    }

    /**
     * All lints.
     *
     * @return List of all lints
     */
    private static Iterable<Lint<XML>> all() {
        try {
            return new Shuffled<Lint<XML>>(
                new Mapped<>(
                    res -> new LtUnlint(
                        new LtByXsl(
                            new InputOf(res.getInputStream()),
                            new InputOf(
                                PkByXsl.XSL_PATTERN.matcher(
                                    PkByXsl.LINTS_PATH.matcher(
                                        res.getURL().toString()
                                    ).replaceAll("eolang/motives")
                                ).replaceAll(".md")
                            )
                        )
                    ),
                    Arrays.asList(
                        new PathMatchingResourcePatternResolver().getResources(
                            "classpath*:org/eolang/lints/**/*.xsl"
                        )
                    )
                )
            );
        } catch (final IOException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
}
