/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import java.util.stream.Collectors;
import org.cactoos.iterable.IterableEnvelope;
import org.cactoos.list.ListOf;

/**
 * WPA lint names.
 * Caches the lint names collection statically to avoid repeated
 * expensive iteration over WpaLints during Program instantiation.
 * @since 0.0.43
 */
final class WpaLintNames extends IterableEnvelope<String> {

    /**
     * Cached WPA lint names.
     */
    private static final Iterable<String> NAMES = new ListOf<>(
        new WpaLints().iterator()
    ).stream()
        .map(Lint::name)
        .collect(Collectors.toList());

    /**
     * Ctor.
     */
    WpaLintNames() {
        super(WpaLintNames.NAMES);
    }
}
