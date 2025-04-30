/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import org.cactoos.list.ListOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link ScopedDefects}.
 *
 * @since 0.0.48
 */
final class ScopedDefectsTest {

    @Test
    void appendsScopeToRuleName() {
        final String marker = "X!";
        MatcherAssert.assertThat(
            String.format(
                "Modified defects do not contain marker: \"%s\", but they should",
                marker
            ),
            new ScopedDefects(
                new ListOf<>(
                    new Defect.Default(
                        "boom-lint",
                        Severity.ERROR,
                        "foo",
                        42,
                        "Foo bar!"
                    )
                ),
                marker
            ),
            Matchers.hasItem(
                Matchers.hasToString(
                    "[foo boom-lint (X!) ERROR]:42 Foo bar!"
                )
            )
        );
    }
}
