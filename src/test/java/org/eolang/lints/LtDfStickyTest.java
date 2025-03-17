/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import fixtures.LtFake;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
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
