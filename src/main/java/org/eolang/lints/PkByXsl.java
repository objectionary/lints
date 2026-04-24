/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import io.github.secretx33.resourceresolver.PathMatchingResourcePatternResolver;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.cactoos.io.InputOf;
import org.cactoos.iterable.IterableEnvelope;
import org.cactoos.iterable.Shuffled;

/**
 * All lints defined by XSLs.
 * Caches all XSL-based lint instances statically to avoid repeated
 * expensive parsing of XSL files during Program instantiation.
 *
 * @since 0.1.0
 */
final class PkByXsl extends IterableEnvelope<Lint> {

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
     * Cached lint instances.
     */
    private static final List<Lint> LINTS = PkByXsl.load();

    /**
     * Ctor.
     */
    PkByXsl() {
        super(new Shuffled<>(PkByXsl.LINTS));
    }

    /**
     * Load all lints once.
     *
     * @return List of all lints
     */
    private static List<Lint> load() {
        try {
            return Arrays.stream(
                new PathMatchingResourcePatternResolver().getResources(
                    "classpath*:org/eolang/lints/**/*.xsl"
                )
            ).map(
                res -> {
                    try {
                        return new LtByXsl(
                            new InputOf(res.getInputStream()),
                            new InputOf(
                                PkByXsl.XSL_PATTERN.matcher(
                                    PkByXsl.LINTS_PATH.matcher(
                                        res.getURL().toString()
                                    ).replaceAll("eolang/motives")
                                ).replaceAll(".md")
                            )
                        );
                    } catch (final IOException ex) {
                        throw new IllegalArgumentException(ex);
                    }
                }
            ).collect(Collectors.toList());
        } catch (final IOException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
}
