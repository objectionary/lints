/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

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
            List.of(
                new LtAsciiOnly(),
                new LtReservedName()
            )
        )
    );

    /**
     * Cached all lint names for LtIncorrectUnlint validation.
     */
    private static final List<String> ALL_NAMES = new ListOf<>(
        new Joined<Lint>(
            MonoLints.LINTS,
            new ListOf<>(
                new LtUnlintNonExistingDefect(MonoLints.LINTS)
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
                List.of(
                    new LtIncorrectUnlint(MonoLints.ALL_NAMES)
                )
            )
        );
    }
}
