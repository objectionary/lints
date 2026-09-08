/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.github.lombrozo.xnav.Xnav;
import com.jcabi.xml.XMLDocument;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link LineOf}.
 * @since 0.0.50
 */
final class LineOfTest {

    @Test
    void returnsNumericLine() {
        MatcherAssert.assertThat(
            "Numeric line is not returned",
            new LineOf(
                LineOfTest.element("<o name=\"x\" line=\"7\"/>")
            ).value(),
            Matchers.equalTo(7)
        );
    }

    @Test
    void returnsZeroWhenLineIsAbsent() {
        MatcherAssert.assertThat(
            "Absent line is not zero",
            new LineOf(LineOfTest.element("<o name=\"x\"/>")).value(),
            Matchers.equalTo(0)
        );
    }

    @Test
    void returnsZeroWhenLineIsNotNumeric() {
        MatcherAssert.assertThat(
            "Non-numeric line should not throw",
            new LineOf(LineOfTest.element("<o name=\"x\" line=\"abc\"/>")).value(),
            Matchers.equalTo(0)
        );
    }

    private static Xnav element(final String tag) {
        return new Xnav(
            new XMLDocument(
                String.format("<object>%s</object>", tag)
            ).inner()
        ).path("/object/o").findFirst().get();
    }
}
