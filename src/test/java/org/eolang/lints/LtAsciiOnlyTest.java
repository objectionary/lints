/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link LtAsciiOnly}.
 * @since 0.0.1
 */
final class LtAsciiOnlyTest {

    @Test
    void explainsMotive() throws IOException {
        MatcherAssert.assertThat(
            "The motive doesn't contain expected string",
            new LtAsciiOnly().motive().contains("# ASCII-Only Characters in Comments"),
            new IsEqual<>(true)
        );
    }
}
