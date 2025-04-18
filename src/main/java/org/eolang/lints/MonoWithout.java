/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import org.cactoos.iterable.IterableEnvelope;

/**
 * Mono lints without lint names.
 * @since 0.0.46
 */
final class MonoWithout extends IterableEnvelope<Lint<XML>> {

    /**
     * Lints.
     */
    private static final Iterable<Lint<XML>> LINTS = new MonoLints();

    /**
     * Ctor.
     *
     * @param names Lints to exclude
     */
    MonoWithout(final String... names) {
        super(new PkMono(new WithoutLints<>(MonoWithout.LINTS, names)));
    }
}
