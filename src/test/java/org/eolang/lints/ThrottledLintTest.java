/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import fixtures.EoProgram;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link ThrottledLint}.
 * @since 0.0.47
 */
final class ThrottledLintTest {

    @Test
    void forwardsName() {
        MatcherAssert.assertThat(
            "ThrottledLint should forward the wrapped lint's name",
            new ThrottledLint(new LtAlways()).name(),
            Matchers.equalTo("always")
        );
    }

    @Test
    void returnsDefectsFromWrappedLint() throws IOException {
        final Collection<Defect> defects = new ThrottledLint(new LtAlways()).defects(
            new EoProgram("org/eolang/lints/foo-without-dot.eo").parse()
        );
        MatcherAssert.assertThat(
            "ThrottledLint should return defects from the wrapped lint",
            defects,
            Matchers.hasSize(1)
        );
    }

    @Test
    void returnsTimeoutDefectWhenLintTakesTooLong() throws IOException {
        final Lint slow = new Lint() {
            @Override
            public String name() {
                return "slow-lint";
            }

            @Override
            public Collection<Defect> defects(final XML xmir) throws IOException {
                try {
                    Thread.sleep(20_000);
                } catch (final InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                return Collections.emptyList();
            }

            @Override
            public String motive() throws IOException {
                return "A slow lint for testing";
            }

            @Override
            public Fix fix() {
                return new FxEmpty();
            }
        };
        final Collection<Defect> defects = new ThrottledLint(slow).defects(
            new EoProgram("org/eolang/lints/foo-without-dot.eo").parse()
        );
        MatcherAssert.assertThat(
            "ThrottledLint should return a single timeout defect when lint exceeds 10s",
            defects,
            Matchers.hasSize(1)
        );
        final Defect defect = defects.iterator().next();
        MatcherAssert.assertThat(
            "Timeout defect should have WARNING severity",
            defect.severity(),
            Matchers.equalTo(Severity.WARNING)
        );
        MatcherAssert.assertThat(
            "Timeout defect should mention the timeout",
            defect.text(),
            Matchers.containsString("timeout")
        );
    }

    @Test
    void returnsEmptyWhenWrappedLintReturnsEmpty() throws IOException {
        final Lint empty = new Lint() {
            @Override
            public String name() {
                return "empty-lint";
            }

            @Override
            public Collection<Defect> defects(final XML xmir) throws IOException {
                return Collections.emptyList();
            }

            @Override
            public String motive() throws IOException {
                return "A lint that returns nothing";
            }

            @Override
            public Fix fix() {
                return new FxEmpty();
            }
        };
        final Collection<Defect> defects = new ThrottledLint(empty).defects(
            new EoProgram("org/eolang/lints/foo-without-dot.eo").parse()
        );
        MatcherAssert.assertThat(
            "ThrottledLint should return empty when wrapped lint returns empty",
            defects,
            Matchers.hasSize(0)
        );
    }

    @Test
    void delegatesFix() {
        MatcherAssert.assertThat(
            "ThrottledLint should delegate fix() to wrapped lint",
            new ThrottledLint(new LtAlways()).fix(),
            Matchers.notNullValue()
        );
    }
}
