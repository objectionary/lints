/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.Sources;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import com.jcabi.xml.XSL;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link MeasuredXsl}.
 *
 * @since 0.0.59
 */
final class MeasuredXslTest {

    @Test
    void transformsUsingOrigin() {
        final XML input = new XMLDocument("<x/>");
        final XML output = new XMLDocument("<transformed/>");
        MatcherAssert.assertThat(
            "transform() must delegate to the origin XSL and return its result",
            new MeasuredXsl("some-rule", new MeasuredXslTest.FakeXsl(output))
                .transform(input),
            Matchers.equalTo(output)
        );
    }

    @Test
    void appliesToUsingOrigin() {
        MatcherAssert.assertThat(
            "applyTo() must delegate to the origin XSL and return its result",
            new MeasuredXsl(
                "some-rule", new MeasuredXslTest.FakeXsl(new XMLDocument("<x/>"))
            ).applyTo(new XMLDocument("<y/>")),
            Matchers.equalTo("applied")
        );
    }

    @Test
    void delegatesWithSources() {
        final MeasuredXslTest.FakeXsl origin = new MeasuredXslTest.FakeXsl(
            new XMLDocument("<x/>")
        );
        MatcherAssert.assertThat(
            "with(Sources) must delegate to the origin XSL",
            new MeasuredXsl("some-rule", origin).with((Sources) null),
            Matchers.sameInstance(origin)
        );
    }

    @Test
    void delegatesWithNameAndValue() {
        final MeasuredXslTest.FakeXsl origin = new MeasuredXslTest.FakeXsl(
            new XMLDocument("<x/>")
        );
        MatcherAssert.assertThat(
            "with(String, Object) must delegate to the origin XSL",
            new MeasuredXsl("some-rule", origin).with("param", "value"),
            Matchers.sameInstance(origin)
        );
    }

    /**
     * Fake XSL that records nothing but returns fixed results.
     *
     * @since 0.0.59
     */
    private static final class FakeXsl implements XSL {

        /**
         * Result returned by {@link #transform(XML)}.
         */
        private final XML result;

        /**
         * Ctor.
         *
         * @param res Transform result
         */
        FakeXsl(final XML res) {
            this.result = res;
        }

        @Override
        public XML transform(final XML xml) {
            return this.result;
        }

        @Override
        public String applyTo(final XML xml) {
            return "applied";
        }

        @Override
        public XSL with(final Sources src) {
            return this;
        }

        @Override
        public XSL with(final String name, final Object value) {
            return this;
        }
    }
}
