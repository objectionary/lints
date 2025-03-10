/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import java.io.IOException;
import java.util.Collection;
import org.cactoos.Func;
import org.cactoos.func.IoCheckedFunc;
import org.cactoos.func.StickyFunc;
import org.cactoos.func.SyncFunc;

/**
 * Lint caching decorator that calls defects method only once. Uses in memory storage for caching.
 *
 * @param <T> The type of entity to analyze
 * @since 0.0.42
 */
final class LtDfSticky<T> implements Lint<T> {

    /**
     * Object wrapped by a decorator.
     */
    private final Lint<T> origin;

    /**
     * Function that caches result of origin.defects().
     */
    private final Func<T, Collection<Defect>> cache;

    /**
     * Ctor.
     * @param origin Object wrapped by a decorator.
     * @param cache Defects cache.
     */
    LtDfSticky(final Lint<T> origin, final Func<T, Collection<Defect>> cache) {
        this.origin = origin;
        this.cache = cache;
    }

    /**
     * Ctor.
     * @param origin Object wrapped by a decorator.
     */
    LtDfSticky(final Lint<T> origin) {
        this(
            origin,
            new SyncFunc<>(new StickyFunc<>(origin::defects))
        );
    }

    @Override
    public String name() {
        return this.origin.name();
    }

    @Override
    public Collection<Defect> defects(final T entity) throws IOException {
        return new IoCheckedFunc<>(this.cache).apply(entity);
    }

    @Override
    public String motive() throws IOException {
        return this.origin.motive();
    }
}
