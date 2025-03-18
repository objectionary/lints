/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 * @checkstyle OuterTypeNumberCheck (200 lines)
 */
package org.eolang.lints;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link LtDfSticky}.
 *
 * @since 0.0.42
 */
final class LtDfStickyTest {

    @Test
    void loadsDefectsOnlyOnce() throws IOException {
        final AtomicInteger count = new AtomicInteger();
        final Lint<Integer> lint = new LtDfSticky<>(
            new LtFake<>(
                entity -> {
                    count.set(count.get() + 1);
                    return Collections.emptyList();
                }
            )
        );
        lint.defects(0);
        lint.defects(0);
        lint.defects(0);
        MatcherAssert.assertThat(
            "Lint.defects() calls amount differs from 1",
            count.get(),
            Matchers.equalTo(1)
        );
    }

    @Test
    void loadsDefectsOnlyOnceForEachEntity() throws IOException {
        final AtomicInteger count = new AtomicInteger();
        final Lint<Integer> lint = new LtDfSticky<>(
            new LtFake<>(
                entity -> {
                    count.set(count.get() + 1);
                    return Collections.emptyList();
                }
            )
        );
        lint.defects(0);
        lint.defects(0);
        lint.defects(1);
        lint.defects(2);
        lint.defects(2);
        MatcherAssert.assertThat(
            "Lint.defects() calls amount differs from 3",
            count.get(),
            Matchers.equalTo(3)
        );
    }

    @Test
    void preventsDefectsLoadingBeforeMethodCall() {
        final AtomicInteger count = new AtomicInteger();
        new LtDfSticky<>(
            new LtFake<>(
                entity -> {
                    count.set(count.get() + 1);
                    return Collections.emptyList();
                }
            )
        );
        MatcherAssert.assertThat(
            "Lt.defects() was called",
            count.get(),
            Matchers.equalTo(0)
        );
    }
}

/**
 * Fake class for Lint class.
 *
 * @param <T> The type of entity to analyze
 * @since 0.0.42
 */
@SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
final class LtFake<T> implements Lint<T> {

    /**
     * Name supplier.
     */
    private final Supplier<String> name;

    /**
     * Defects supplier.
     */
    private final Function<T, Collection<Defect>> defects;

    /**
     * Motive supplier.
     */
    private final Supplier<String> motive;

    /**
     * Constructor.
     * @param name Lint name supplier.
     * @param defects Lint defects supplier.
     * @param motive Lint motive supplier.
     */
    LtFake(
        final Supplier<String> name,
        final Function<T, Collection<Defect>> defects,
        final Supplier<String> motive
    ) {
        this.name = name;
        this.defects = defects;
        this.motive = motive;
    }

    /**
     * Constructor. Name and motive are provided by value.
     * @param name Lint name value.
     * @param defects Lint defects supplier.
     * @param motive Lint motive value.
     */
    LtFake(
        final String name,
        final Function<T, Collection<Defect>> defects,
        final String motive
    ) {
        this(() -> name, defects, () -> motive);
    }

    /**
     * Constructor. Name and motive are hardcoded.
     * @param defects Lint defects supplier.
     */
    LtFake(final Function<T, Collection<Defect>> defects) {
        this("Lint", defects, "Motive");
    }

    @Override
    public String name() {
        return this.name.get();
    }

    @Override
    public Collection<Defect> defects(final T entity) throws IOException {
        return this.defects.apply(entity);
    }

    @Override
    public String motive() throws IOException {
        return this.motive.get();
    }
}
