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
import java.util.stream.Collectors;
import org.cactoos.io.InputOf;
import org.cactoos.io.ResourceOf;
import org.cactoos.iterable.IterableEnvelope;
import org.cactoos.iterable.Shuffled;
import org.cactoos.text.FormattedText;

/**
 * All lints defined by XSLs.
 * Caches all XSL-based lint instances statically to avoid repeated
 * expensive parsing of XSL files during Program instantiation.
 *
 * @since 0.1.0
 */
final class PkByXsl extends IterableEnvelope<Lint> {

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

    private static Lint lint(final Resource res) {
        try {
            final String name = res.getURL().toString()
                .replaceAll(".*org/eolang/lints/", "")
                .replaceAll("\\.xsl$", "");
            return new LtByXsl(
                new InputOf(res.getInputStream()),
                new ResourceOf(
                    new FormattedText("org/eolang/motives/%s.md", name)
                ),
                new FxResource(
                    new FormattedText("org/eolang/fixes/%s.xsl", name)
                )
            );
        } catch (final IOException ex) {
            throw new IllegalArgumentException(
                "Failed to build an XSL lint from the classpath",
                ex
            );
        }
    }
}
