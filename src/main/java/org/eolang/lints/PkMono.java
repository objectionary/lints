/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import javax.annotation.concurrent.ThreadSafe;
import org.cactoos.iterable.IterableEnvelope;
import org.cactoos.iterable.Joined;
import org.cactoos.iterable.Mapped;
import org.cactoos.iterable.Sticky;
import org.cactoos.list.ListOf;

/**
 * Collection of lints for individual XML files, provided
 * by the {@link Source} class.
 *
 * <p>This class is thread-safe.</p>
 *
 * @since 0.23
 */
@ThreadSafe
final class PkMono extends IterableEnvelope<Lint> {

    /**
     * All XML-based lints.
     */
    private static final Iterable<Lint> LINTS = new MonoLints();

    /**
     * Default ctor.
     */
    PkMono() {
        this(PkMono.LINTS);
    }

    /**
     * Ctor.
     * @param lints Lints
     */
    PkMono(final Iterable<Lint> lints) {
        super(PkMono.build(lints));
    }

    private static Iterable<Lint> build(final Iterable<Lint> lints) {
        final Iterable<Lint> cached = new Sticky<>(
            new Mapped<Lint>(LtDfSticky::new, lints)
        );
        return new Joined<Lint>(
            new Mapped<Lint>(LtUnlint::new, cached),
            new ListOf<>(
                new LtDfSticky(new LtUnlint(new LtUnlintNonExistingDefect(cached)))
            )
        );
    }
}
