/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
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
 * @since 0.0.42
 */
final class LtDfStickyTest {

    @Test
    void loadsDefectsOnlyOnce() throws IOException {
        final LtCounter counter = new LtDfStickyTest.LtCounter();
        final Lint lint = new LtDfSticky(new LtDfStickyTest.LtFake(counter));
        final XML xmir = new XMLDocument("<o/>");
        lint.defects(xmir);
        lint.defects(xmir);
        lint.defects(xmir);
        MatcherAssert.assertThat(
            "Lint.defects() calls amount differs from 1",
            counter.count(),
            Matchers.equalTo(1)
        );
    }

    @Test
    void loadsDefectsOnlyOnceForEachEntity() throws IOException {
        final LtCounter counter = new LtDfStickyTest.LtCounter();
        final Lint lint = new LtDfSticky(new LtDfStickyTest.LtFake(counter));
        final XML one = new XMLDocument("<o id='1'/>");
        final XML three = new XMLDocument("<o id='3'/>");
        lint.defects(one);
        lint.defects(one);
        lint.defects(new XMLDocument("<o id='2'/>"));
        lint.defects(three);
        lint.defects(three);
        MatcherAssert.assertThat(
            "Lint.defects() calls amount differs from 3",
            counter.count(),
            Matchers.equalTo(3)
        );
    }

    @Test
    void preventsDefectsLoadingBeforeMethodCall() {
        final LtCounter counter = new LtDfStickyTest.LtCounter();
        new LtDfSticky(new LtDfStickyTest.LtFake(counter));
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
    private static final class LtCounter implements Function<XML, Collection<Defect>> {

        /**
         * Counter value.
         */
        private int cnt;

        @Override
        public Collection<Defect> apply(final XML entity) {
            this.cnt = this.cnt + 1;
            return Collections.emptyList();
        }

        int count() {
            return this.cnt;
        }
    }

    /**
     * Fake class for Lint class.
     * @since 0.0.42
     */
    private static final class LtFake implements Lint {

        /**
         * Name supplier.
         */
        private final Supplier<String> nme;

        /**
         * Defects supplier.
         */
        private final Function<XML, Collection<Defect>> aggregated;

        /**
         * Motive supplier.
         */
        private final Supplier<String> mtve;

        /**
         * Constructor. Name and motive are hardcoded.
         * @param defects Lint defects supplier
         */
        LtFake(final Function<XML, Collection<Defect>> defects) {
            this(() -> "Lint", defects, () -> "Motive");
        }

        /**
         * Constructor.
         * @param name Lint name supplier
         * @param defects Lint defects supplier
         * @param motive Lint motive supplier
         */
        LtFake(
            final Supplier<String> name,
            final Function<XML, Collection<Defect>> defects,
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
        public Collection<Defect> defects(final XML entity) throws IOException {
            return this.aggregated.apply(entity);
        }

        @Override
        public String motive() throws IOException {
            return this.mtve.get();
        }

        @Override
        public Fix fix() {
            return new FxEmpty();
        }
    }
}
