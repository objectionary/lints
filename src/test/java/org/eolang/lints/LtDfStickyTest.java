/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
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
        final LtCounter counter = new LtCounter();
        final Lint<Integer> lint = new LtDfSticky<>(
            new LtFake<>(counter)
        );
        lint.defects(0);
        lint.defects(0);
        lint.defects(0);
        MatcherAssert.assertThat(
            "Lint.defects() calls amount differs from 1",
            counter.count(),
            Matchers.equalTo(1)
        );
    }

    @Test
    void loadsDefectsOnlyOnceForEachEntity() throws IOException {
        final LtCounter counter = new LtCounter();
        final Lint<Integer> lint = new LtDfSticky<>(
            new LtFake<>(counter)
        );
        lint.defects(0);
        lint.defects(0);
        lint.defects(1);
        lint.defects(2);
        lint.defects(2);
        MatcherAssert.assertThat(
            "Lint.defects() calls amount differs from 3",
            counter.count(),
            Matchers.equalTo(3)
        );
    }

    @Test
    void preventsDefectsLoadingBeforeMethodCall() {
        final LtCounter counter = new LtCounter();
        new LtDfSticky<>(
            new LtFake<>(counter)
        );
        MatcherAssert.assertThat(
            "Lt.defects() was called",
            counter.count(),
            Matchers.equalTo(0)
        );
    }

    /**
     * Counter for lint calls.
     * @since 0.0.42
     */
    private static final class LtCounter implements Function<Object, Collection<Defect>> {
        /**
         * Counter value.
         */
        private int cnt;

        @Override
        public Collection<Defect> apply(final Object entity) {
            this.cnt = this.cnt + 1;
            return Collections.emptyList();
        }

        public int count() {
            return this.cnt;
        }
    }

    /**
     * Fake class for Lint class.
     *
     * @param <T> The type of entity to analyze
     * @since 0.0.42
     */
    private static final class LtFake<T> implements Lint<T> {

        /**
         * Name supplier.
         */
        private final Supplier<String> nme;

        /**
         * Defects supplier.
         */
        private final Function<T, Collection<Defect>> aggregated;

        /**
         * Motive supplier.
         */
        private final Supplier<String> mtve;

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
        @SuppressWarnings("unchecked")
        LtFake(final Function<?, Collection<Defect>> defects) {
            this("Lint", (Function<T, Collection<Defect>>) defects, "Motive");
        }

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
            this.nme = name;
            this.aggregated = defects;
            this.mtve = motive;
        }

        @Override
        public String name() {
            return this.nme.get();
        }

        @Override
        public Collection<Defect> defects(final T entity) throws IOException {
            return this.aggregated.apply(entity);
        }

        @Override
        public String motive() throws IOException {
            return this.mtve.get();
        }
    }
}
