/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import java.util.stream.Collectors;
import org.cactoos.iterable.IterableEnvelope;
import org.cactoos.list.ListOf;

/**
 * WPA lint names.
 * @since 0.0.43
 */
final class WpaLintNames extends IterableEnvelope<String> {
    /**
     * Ctor.
     */
    WpaLintNames() {
        super(
            new ListOf<>(new WpaLints().iterator())
                .stream()
                .map(Lint::name)
                .collect(Collectors.toList())
        );
    }
}
