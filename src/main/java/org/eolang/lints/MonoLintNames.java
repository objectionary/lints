/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import java.util.stream.Collectors;
import org.cactoos.iterable.IterableEnvelope;
import org.cactoos.list.ListOf;

/**
 * Mono lint names.
 * @since 0.0.43
 */
final class MonoLintNames extends IterableEnvelope<String> {
    /**
     * Ctor.
     */
    MonoLintNames() {
        super(
            new ListOf<>(
                new MonoLints().iterator()
            ).stream()
                .map(Lint::name)
                .collect(Collectors.toList())
        );
    }
}
