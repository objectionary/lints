/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import java.util.stream.Collectors;
import org.cactoos.iterable.IterableEnvelope;
import org.cactoos.list.ListOf;

/**
 * Mono lint names.
 * Caches the lint names collection statically to avoid repeated
 * expensive iteration over MonoLints during Program instantiation.
 * @since 0.0.43
 */
final class MonoLintNames extends IterableEnvelope<String> {

    /**
     * Cached mono lint names.
     */
    private static final Iterable<String> NAMES = new ListOf<>(
        new MonoLints().iterator()
    ).stream()
        .map(Lint::name)
        .collect(Collectors.toList());

    /**
     * Ctor.
     */
    MonoLintNames() {
        super(MonoLintNames.NAMES);
    }
}
