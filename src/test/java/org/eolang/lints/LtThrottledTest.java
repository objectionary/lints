/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link LtThrottled}.
 *
 * @since 0.6.1
 */
final class LtThrottledTest {

    @Test
    void returnsDefectsOfFastLint() throws IOException {
        MatcherAssert.assertThat(
            "Defects of a lint that finishes in time must be passed through",
            new LtThrottled(new LtAlways(), Duration.ofSeconds(10L))
                .defects(new XMLDocument("<o/>")),
            Matchers.hasSize(1)
        );
    }

    @Test
    void reportsTimeoutOfSlowLint() throws IOException {
        final CountDownLatch never = new CountDownLatch(1);
        final Collection<Defect> defects = new LtThrottled(
            new LtThrottledTest.LtSlow(never), Duration.ofMillis(100L)
        ).defects(new XMLDocument("<o/>"));
        never.countDown();
        MatcherAssert.assertThat(
            "A lint that runs for too long must be reported as a single warning",
            defects.stream()
                .map(dft -> String.format("%s %s %s", dft.rule(), dft.severity(), dft.text()))
                .collect(Collectors.toList()),
            Matchers.contains(
                Matchers.startsWith("slow WARNING The lint did not finish in 100 ms")
            )
        );
    }

    @Test
    void interruptsSlowLint() throws Exception {
        final LtThrottledTest.LtSlow slow = new LtThrottledTest.LtSlow(new CountDownLatch(1));
        new LtThrottled(slow, Duration.ofMillis(100L)).defects(new XMLDocument("<o/>"));
        MatcherAssert.assertThat(
            "A lint that runs for too long must be interrupted",
            slow.interrupted.await(5L, TimeUnit.SECONDS),
            Matchers.is(true)
        );
    }

    @Test
    void rethrowsFailureOfLint() {
        Assertions.assertThrows(
            IllegalStateException.class,
            () -> new LtThrottled(new LtThrottledTest.LtBroken(), Duration.ofSeconds(10L))
                .defects(new XMLDocument("<o/>")),
            "A failure of the lint must reach the caller as is"
        );
    }

    @Test
    void keepsNameOfLint() {
        MatcherAssert.assertThat(
            "The name of the lint must stay the same",
            new LtThrottled(new LtAlways()).name(),
            Matchers.equalTo("always")
        );
    }

    /**
     * Lint that waits until it is released or interrupted.
     *
     * @since 0.6.1
     */
    private static final class LtSlow implements Lint {

        /**
         * Released when the lint may finish.
         */
        private final CountDownLatch release;

        /**
         * Released when the lint gets interrupted.
         */
        private final CountDownLatch interrupted;

        /**
         * Ctor.
         *
         * @param release Released when the lint may finish
         */
        LtSlow(final CountDownLatch release) {
            this.release = release;
            this.interrupted = new CountDownLatch(1);
        }

        @Override
        public String name() {
            return "slow";
        }

        @Override
        public Collection<Defect> defects(final XML xmir) {
            try {
                this.release.await();
            } catch (final InterruptedException ex) {
                this.interrupted.countDown();
                Thread.currentThread().interrupt();
            }
            return Collections.emptyList();
        }

        @Override
        public String motive() {
            return "";
        }

        @Override
        public Fix fix() {
            return new FxEmpty();
        }
    }

    /**
     * Lint that always fails.
     *
     * @since 0.6.1
     */
    private static final class LtBroken implements Lint {

        @Override
        public String name() {
            return "broken";
        }

        @Override
        public Collection<Defect> defects(final XML xmir) {
            throw new IllegalStateException("broken on purpose");
        }

        @Override
        public String motive() {
            return "";
        }

        @Override
        public Fix fix() {
            return new FxEmpty();
        }
    }
}
