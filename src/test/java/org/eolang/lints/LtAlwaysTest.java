/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import java.io.IOException;
import org.eolang.parser.EoSyntax;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link LtAlways}.
 *
 * @since 0.0.1
 */
final class LtAlwaysTest {

    @Test
    void complainsAlways() throws IOException {
        MatcherAssert.assertThat(
            "didn't return one defect",
            new LtAlways().defects(
                new EoSyntax("# first\n[] > foo\n").parsed()
            ),
            Matchers.hasSize(1)
        );
    }

    @Test
    void returnsCorrectLintName() {
        MatcherAssert.assertThat(
            "incorrect name of the lint",
            new LtAlways().name(),
            Matchers.equalTo("always")
        );
    }
}
