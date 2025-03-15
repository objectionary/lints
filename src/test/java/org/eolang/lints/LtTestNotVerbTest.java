/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.yegor256.MayBeSlow;
import com.yegor256.Together;
import matchers.DefectMatcher;
import org.cactoos.set.SetOf;
import org.eolang.parser.EoSyntax;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests for {@link LtTestNotVerb}.
 *
 * @since 0.0.22
 */
final class LtTestNotVerbTest {

    @ExtendWith(MayBeSlow.class)
    @ParameterizedTest
    @ValueSource(
        strings = {
            "itIsTrue",
            "testing",
            "this-is-test",
            "it-works",
            "nothing-happened",
            "something-is-wrong",
            "will-fail-eventually",
            "always-returns-true",
            "was-lost-forever",
            "nobody-knows-why",
            "should-not-pass",
            "once-upon-a-time",
            "must-do-better",
            "was-a-trap",
            "dont-look-here",
            "never-saw-it-coming",
            "this-time-for-sure",
            "there-it-goes",
            "it-is-fine-probably",
            "maybe-next-time",
            "well-this-is-awkward",
            "why-this-again",
            "here-we-go-again",
            "it-was-working-before",
            "expected-the-unexpected",
            "it-seems-fine",
            "suddenly-works",
            "too-late-now",
            "could-not-care-less",
            "it-just-works",
            "dont-push-that-button",
            "error-404-not-found",
            "who-did-this",
            "it-has-a-plan",
            "will-never-finish",
            "accidentally-passed",
            "i-think-its-ok",
            "chicken-as-expected",
            "please-reboot",
            "hope-it-works"
        }
    )
    void catchesBadName(final String name) throws Exception {
        MatcherAssert.assertThat(
            "Defects size doesn't match with expected",
            new LtTestNotVerb().defects(
                new EoSyntax(
                    String.join(
                        "\n",
                        "+tests",
                        "",
                        "# Test",
                        String.format("[] > %s", name),
                        "  42 > @"
                    )
                ).parsed()
            ),
            Matchers.allOf(
                Matchers.<Defect>iterableWithSize(1),
                Matchers.everyItem(new DefectMatcher())
            )
        );
    }

    @ExtendWith(MayBeSlow.class)
    @ParameterizedTest
    @ValueSource(
        strings = {
            "generates-report",
            "locks-branch",
            "parses-dom",
            "prints-data",
            "runs",
            "works-as-expected",
            "breaks-hearts",
            "crashes-again",
            "forgets-everything",
            "has-been-found",
            "looks-fine-to-me",
            "returns-something-strange",
            "disappears-silently",
            "follows-the-rules",
            "finds-nothing-at-all",
            "sounds-legit",
            "sleeps-forever",
            "makes-zero-sense",
            "runs-in-circles",
            "is-never-called",
            "is-kind-of-slow",
            "is-totally-broken",
            "is-almost-correct"
        }
    )
    void allowsGoodNames(final String name) throws Exception {
        MatcherAssert.assertThat(
            "Defects are not empty, but they shouldn't be",
            new LtTestNotVerb().defects(
                new EoSyntax(
                    String.join(
                        "\n",
                        "+tests",
                        "",
                        "# Test",
                        String.format("[] > %s", name),
                        "  42 > @"
                    )
                ).parsed()
            ),
            Matchers.hasSize(0)
        );
    }

    @Test
    @ExtendWith(MayBeSlow.class)
    void lintsRegexTests() throws Exception {
        MatcherAssert.assertThat(
            "Defects size doesn't match with expected",
            new LtTestNotVerb().defects(
                new EoSyntax(
                    String.join(
                        "\n",
                        "+tests",
                        "",
                        "# This unit test is supposed to check the functionality of the corresponding object.",
                        "[] > regex-contains-valid-groups-on-each-matched-block",
                        "  ((regex \"/([a-z]+)([1-9]{1})/\").compiled.match \"!hello1!world2\").next > first",
                        "  first.next > second",
                        "  and. > @",
                        "    and.",
                        "      and.",
                        "        first.groups-count.eq 3",
                        "        (first.group 1).eq \"hello\"",
                        "      (first.group 2).eq \"1\"",
                        "    and.",
                        "      and.",
                        "        second.groups-count.eq 3",
                        "        (second.group 1).eq \"world\"",
                        "      (second.group 2).eq \"2\""
                    )
                ).parsed()
            ),
            Matchers.hasSize(1)
        );
    }

    @Tag("deep")
    @RepeatedTest(2)
    void lintsInMultipleThreads() {
        MatcherAssert.assertThat(
            "wrong number of defects found, in parallel",
            new SetOf<>(
                new Together<>(
                    t -> new LtTestNotVerb().defects(
                        new EoSyntax(
                            String.join(
                                "\n",
                                "+tests",
                                "",
                                "# Unit test",
                                "[] > matches-regex-against-the-pattern",
                                "  (regex \"/[a-z]+/\").compiled.matches \"hello\" > @"
                            )
                        ).parsed()
                    ).size()
                ).asList()
            ).size(),
            Matchers.equalTo(1)
        );
    }
}
