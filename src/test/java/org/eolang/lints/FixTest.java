/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import fixtures.FixPack;
import org.eolang.jucs.ClasspathSource;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * Tests {@link Fix}.
 * @since 0.2.1
 */
final class FixTest {

    @ParameterizedTest
    @ClasspathSource(value = "org/eolang/lints/fixes/unsorted-metas/", glob = "**.yaml")
    void fixesUnsortedMetas(final String yaml) throws Exception {
        final FixPack pack = new FixPack(yaml);
        MatcherAssert.assertThat(
            "Full XMIR after fix must exactly match the expected XMIR",
            pack.fixed(),
            Matchers.equalTo(pack.expected())
        );
    }
}
