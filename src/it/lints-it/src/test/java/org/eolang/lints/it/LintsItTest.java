/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints.it;

import com.jcabi.xml.XMLDocument;
import java.io.IOException;
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
    void lintsProgram() throws IOException {
        MatcherAssert.assertThat(
            "passes with no exceptions",
            new Project(new XMLDocument("<program name='it'/>")).singleDefects(),
            Matchers.notNullValue()
        );
    }
}
