/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import io.github.secretx33.resourceresolver.PathMatchingResourcePatternResolver;
import io.github.secretx33.resourceresolver.Resource;
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
     * @return List of all lints
     */
    @SuppressWarnings("PMD.UnnecessaryLocalRule")
    private static List<Lint> load() {
        try {
            return Arrays.stream(
                new PathMatchingResourcePatternResolver().getResources(
                    "classpath*:org/eolang/lints/**/*.xsl"
                )
            ).map(
                PkByXsl::lint
            ).collect(Collectors.toList());
        } catch (final IOException ex) {
            throw new IllegalArgumentException(
                "Failed to load XSL lints from the classpath",
                ex
            );
        }
    }

    /**
     * Build a lint for the given XSL resource.
     * @param res Resource with XSL
     * @return Lint
     */
    private static Lint lint(final Resource res) {
        try {
            final String url = res.getURL().toString();
            return new LtByXsl(
                new InputOf(res.getInputStream()),
                new InputOf(
                    PkByXsl.XSL_PATTERN.matcher(
                        PkByXsl.LINTS_PATH.matcher(url).replaceAll("eolang/motives")
                    ).replaceAll(".md")
                ),
                new FxResource(
                    String.format(
                        "org/eolang/fixes/%s.xsl",
                        url.replaceAll(".*org/eolang/lints/", "").replaceAll("\\.xsl$", "")
                    )
                )
            );
        } catch (final IOException ex) {
            throw new IllegalArgumentException(
                "Failed to build a fix for an XSL lint",
                ex
            );
        }
    }
}
