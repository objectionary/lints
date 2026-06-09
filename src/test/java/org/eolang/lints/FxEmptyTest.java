/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link FxEmpty}.
 * @since 0.2.1
 */
final class FxEmptyTest {

    @Test
    void returnsXmirUnchanged() throws Exception {
        final XML xmir = new XMLDocument("<o/>");
        MatcherAssert.assertThat(
            "FxEmpty must return the same XMIR unchanged",
            new FxEmpty().apply(xmir),
            Matchers.equalTo(xmir)
        );
    }
}
