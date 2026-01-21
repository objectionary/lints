/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
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
                    res ->
                        new LtByXsl(
                            new InputOf(res.getInputStream()),
                            PkByXsl.XSL_PATTERN.matcher(
                                PkByXsl.LINTS_PATH.matcher(
                                    res.getURL().toString()
                                ).replaceAll("eolang/motives")
                            ).replaceAll(".md")
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
