/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.cactoos.iterable.IterableEnvelope;
import org.cactoos.iterable.Joined;
import org.cactoos.iterable.Shuffled;
import org.cactoos.list.ListOf;

/**
 * Mono lints.
 * Mono lints represent a list of lints for single XMIR scope.
 * @since 0.0.43
 */
final class MonoLints extends IterableEnvelope<Lint> {

    /**
     * All XML-based lints.
     */
    private static final Iterable<Lint> LINTS = new Shuffled<>(
        new Joined<Lint>(
            new PkByXsl(),
            MonoLints.mono()
        )
    );

    /**
     * Cached all lint names for LtIncorrectUnlint validation.
     */
    private static final List<String> ALL_NAMES = new ListOf<>(
        new Joined<Lint>(
            MonoLints.LINTS,
            new ListOf<>(
                new LtUnlintNonExistingDefect(MonoLints.LINTS),
                new LtIncorrectUnlint(List.of())
            )
        )
    ).stream()
        .map(Lint::name)
        .collect(Collectors.toList());

    /**
     * Ctor.
     */
    MonoLints() {
        super(
            new Joined<Lint>(
                MonoLints.LINTS,
                new ListOf<>(
                    new LtIncorrectUnlint(MonoLints.ALL_NAMES)
                )
            )
        );
    }

    /**
     * Java-based lints.
     * @return Java-based lints
     */
    private static List<Lint> mono() {
        try {
            return List.of(
                new LtAsciiOnly(),
                new LtReservedName(),
                new LtSyntaxVersion(),
                new LtTestNotVerb()
            );
        } catch (final IOException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
}
