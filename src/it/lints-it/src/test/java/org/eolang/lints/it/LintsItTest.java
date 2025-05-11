/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints.it;

import com.jcabi.xml.XMLDocument;
import java.io.IOException;
import java.util.Map;
import org.eolang.lints.Program;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Integration test for lints.
 *
 * @since 0.0.1
 */
final class LintsItTest {

    @Test
    void lintsSource() throws IOException {
        MatcherAssert.assertThat(
            "passes with no exceptions",
            new Program(
                Map.of(
                    "main.eo",
                    new XMLDocument("<object/>")
                )
            ).defects(),
            Matchers.notNullValue()
        );
    }
}
