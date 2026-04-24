/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.Collection;
import org.cactoos.Func;
import org.cactoos.func.IoCheckedFunc;
import org.cactoos.func.StickyFunc;
import org.cactoos.func.SyncFunc;

/**
 * Lint caching decorator that calls defects method only once. Uses in memory storage for caching.
 *
 * <p>This class is thread-safe.</p>
 *
 * @since 0.0.42
 */
final class LtDfSticky implements Lint {

    /**
     * Object wrapped by a decorator.
     */
    private final Lint origin;

    /**
     * Function that caches result of origin.defects().
     */
    private final Func<XML, Collection<Defect>> cache;

    /**
     * Ctor.
     * @param origin Object wrapped by a decorator.
     */
    LtDfSticky(final Lint origin) {
        this(
            origin,
            new SyncFunc<>(new StickyFunc<>(origin::defects))
        );
    }

    /**
     * Ctor.
     * @param origin Object wrapped by a decorator.
     * @param cache Defects cache.
     */
    LtDfSticky(final Lint origin, final Func<XML, Collection<Defect>> cache) {
        this.origin = origin;
        this.cache = cache;
    }

    @Override
    public String name() {
        return this.origin.name();
    }

    @Override
    public Collection<Defect> defects(final XML xmir) throws IOException {
        return new IoCheckedFunc<>(this.cache).apply(xmir);
    }

    @Override
    public String motive() throws IOException {
        return this.origin.motive();
    }
}
