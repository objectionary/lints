/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.github.lombrozo.xnav.Xnav;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link ParentObject}.
 *
 * @since 0.0.50
 */
final class ParentObjectTest {

    @Test
    void goesUpToParent() {
        MatcherAssert.assertThat(
            "Parent object was not retrieved as expected",
            new ParentObject(
                new Xnav(
                    "<foo ttt='x'>",
                    "  <bar ttt='y'/>",
                    "</foo>"
                ).element("foo").element("bar")
            ).value().attribute("ttt").text().get(),
            Matchers.equalTo(
                "x"
            )
        );
    }
}
