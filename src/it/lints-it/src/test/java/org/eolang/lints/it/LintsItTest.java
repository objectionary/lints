/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints.it;

import com.jcabi.xml.XMLDocument;
import java.io.IOException;
import org.eolang.lints.Source;
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
            new Source(new XMLDocument("<object><o name='foo'/></object>")).defects(),
            Matchers.notNullValue()
        );
    }
}
