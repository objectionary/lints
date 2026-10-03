/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XMLDocument;
import java.io.IOException;
import org.cactoos.list.ListOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link LtIncorrectUnlint}.
 *
 * @since 0.0.38
 */
final class LtIncorrectUnlintTest {

    @Test
    void reportsSingleLineSelectorsOutsideIntegerRange() throws IOException {
        MatcherAssert.assertThat(
            "An unrepresentable line selector should be reported as invalid",
            new LtIncorrectUnlint(new ListOf<>("ascii-only")).defects(
                new XMLDocument(
                    "<object><metas><meta line='1'><head>unlint</head>"
                        + "<tail>ascii-only:2147483648</tail></meta></metas></object>"
                )
            ),
            Matchers.hasSize(1)
        );
    }

    @Test
    void reportsRangeSelectorsOutsideIntegerRange() throws IOException {
        MatcherAssert.assertThat(
            "An unrepresentable range selector should be reported as invalid",
            new LtIncorrectUnlint(new ListOf<>("ascii-only")).defects(
                new XMLDocument(
                    "<object><metas><meta line='1'><head>unlint</head>"
                        + "<tail>ascii-only:1-2147483648</tail></meta></metas></object>"
                )
            ),
            Matchers.hasSize(1)
        );
    }

    @Test
    void treatsRegexCharactersInKnownRuleNamesLiterally() throws IOException {
        MatcherAssert.assertThat(
            "A valid rule name must not be interpreted as a regex",
            new LtIncorrectUnlint(new ListOf<>("[bad")).defects(
                new XMLDocument(
                    "<object><metas><meta line='1'><head>unlint</head>"
                        + "<tail>[bad:1</tail></meta></metas></object>"
                )
            ),
            Matchers.emptyIterable()
        );
    }
}
