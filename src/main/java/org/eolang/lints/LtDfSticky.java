/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

/**
 * Lint caching decorator that calls defects method only once. Uses in memory storage for caching.
 *
 * <p>The cache is bounded and keyed by the XMIR content, so a long-lived JVM does not run
 * out of memory while repeatedly analyzing the same documents.</p>
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
     * Cache of the defects, keyed by the document content.
     */
    private final Cache<String, Collection<Defect>> cache;

    /**
     * Ctor.
     * @param origin Object wrapped by a decorator
     * @checkstyle ConstructorsCodeFreeCheck (4 lines)
     */
    LtDfSticky(final Lint origin) {
        this.origin = origin;
        this.cache = LtDfSticky.newCache();
    }

    @Override
    public String name() {
        return this.origin.name();
    }

    @Override
    public Collection<Defect> defects(final XML xmir) throws IOException {
        final Collection<Defect> defects;
        try {
            defects = this.cache.get(
                xmir.toString(),
                () -> this.origin.defects(xmir)
            );
        } catch (final ExecutionException exc) {
            throw LtDfSticky.propagate(exc);
        }
        return defects;
    }

    @Override
    public String motive() throws IOException {
        return this.origin.motive();
    }

    @Override
    public Fix fix() {
        return this.origin.fix();
    }

    private static Cache<String, Collection<Defect>> newCache() {
        return CacheBuilder.newBuilder()
            .maximumSize(1_000)
            .build();
    }

    private static IOException propagate(final ExecutionException exc) {
        final IOException result;
        if (exc.getCause() instanceof IOException) {
            result = (IOException) exc.getCause();
        } else {
            result = new IOException(exc);
        }
        return result;
    }
}
