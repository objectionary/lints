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
                LtIncorrectUnlintTest.unlint("ascii-only:2147483648")
            ),
            Matchers.hasSize(1)
        );
    }

    @Test
    void reportsRangeSelectorsOutsideIntegerRange() throws IOException {
        MatcherAssert.assertThat(
            "An unrepresentable range selector should be reported as invalid",
            new LtIncorrectUnlint(new ListOf<>("ascii-only")).defects(
                LtIncorrectUnlintTest.unlint("ascii-only:1-2147483648")
            ),
            Matchers.hasSize(1)
        );
    }

    @Test
    void treatsRegexCharactersInKnownRuleNamesLiterally() throws IOException {
        MatcherAssert.assertThat(
            "A valid rule name must not be interpreted as a regex",
            new LtIncorrectUnlint(new ListOf<>("[bad")).defects(
                LtIncorrectUnlintTest.unlint("[bad:1")
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void reportsInvertedRangeSelectors() throws IOException {
        MatcherAssert.assertThat(
            "An inverted range selector should be reported as invalid",
            new LtIncorrectUnlint(new ListOf<>("ascii-only")).defects(
                LtIncorrectUnlintTest.unlint("ascii-only:20-10")
            ),
            Matchers.hasSize(1)
        );
    }

    @Test
    void reportsInvertedRangeWithLargeNumbers() throws IOException {
        MatcherAssert.assertThat(
            "A large inverted range selector should be reported as invalid",
            new LtIncorrectUnlint(new ListOf<>("ascii-only")).defects(
                LtIncorrectUnlintTest.unlint("ascii-only:500-100")
            ),
            Matchers.hasSize(1)
        );
    }

    @Test
    void acceptsEqualStartAndFinishInRanges() throws IOException {
        MatcherAssert.assertThat(
            "A range where start equals finish should be accepted",
            new LtIncorrectUnlint(new ListOf<>("ascii-only")).defects(
                LtIncorrectUnlintTest.unlint("ascii-only:10-10")
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void acceptsValidAscendingRanges() throws IOException {
        MatcherAssert.assertThat(
            "An ascending range should be accepted",
            new LtIncorrectUnlint(new ListOf<>("ascii-only")).defects(
                LtIncorrectUnlintTest.unlint("ascii-only:10-20")
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void reportsEmptyUnlintTail() throws IOException {
        MatcherAssert.assertThat(
            "An empty unlint tail should be reported as invalid",
            new LtIncorrectUnlint(new ListOf<>("ascii-only")).defects(
                LtIncorrectUnlintTest.unlint("")
            ),
            Matchers.hasSize(1)
        );
    }

    @Test
    void reportsMissingUnlintTail() throws IOException {
        MatcherAssert.assertThat(
            "A missing unlint tail should be reported as invalid",
            new LtIncorrectUnlint(new ListOf<>("ascii-only")).defects(
                new XMLDocument(
                    String.join(
                        "",
                        "<object><metas><meta line='1'>",
                        "<head>unlint</head>",
                        "</meta></metas></object>"
                    )
                )
            ),
            Matchers.hasSize(1)
        );
    }

    @Test
    void providesClearMessageForEmptyUnlint() throws IOException {
        MatcherAssert.assertThat(
            "The error message must not refer to fake lint name unknown",
            new LtIncorrectUnlint(new ListOf<>("ascii-only")).defects(
                LtIncorrectUnlintTest.unlint("")
            ).iterator().next().text(),
            Matchers.equalTo(
                String.join(
                    " ",
                    "The \"+unlint\" meta is empty, which is prohibited;",
                    "specify a lint name to suppress"
                )
            )
        );
    }

    private static XMLDocument unlint(final String selector) {
        return new XMLDocument(
            String.join(
                "",
                "<object><metas><meta line='1'><head>unlint</head><tail>",
                selector,
                "</tail></meta></metas></object>"
            )
        );
    }
}
