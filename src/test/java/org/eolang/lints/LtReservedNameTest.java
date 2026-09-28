/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import fixtures.EoProgram;
import org.cactoos.list.ListOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link LtReservedName}.
 * Most scenarios are covered by pure EO test packs under
 * {@code src/test/resources/org/eolang/lints/packs/single/reserved-name/},
 * exercised via {@link LtByXslTest#checksAllLints(String, String)} with the
 * generic {@code params} pack key. The two tests below stay here because
 * they depend on the real reserved-name list downloaded by the
 * {@code reserved} Maven profile, which is unavailable to the generic
 * pack runner.
 *
 * @since 0.0.44
 */
final class LtReservedNameTest {

    @Tag("reserved")
    @Test
    void scansReservedFromHome() throws Exception {
        MatcherAssert.assertThat(
            "Defects size does not match with expected",
            new LtReservedName().defects(
                new EoProgram("org/eolang/lints/reserved-bar-stdout.eo").parse()
            ),
            Matchers.hasSize(1)
        );
    }

    @Tag("reserved")
    @Test
    void scansReservedFromHomeWithCorrectMessage() throws Exception {
        MatcherAssert.assertThat(
            "Defect message does not match with expected",
            new ListOf<>(
                new LtReservedName().defects(
                    new EoProgram("org/eolang/lints/reserved-baz-stdout.eo").parse()
                )
            ).get(0).text(),
            Matchers.equalTo(
                "Object name \"stdout\" is already reserved by object in the \"stdout.eo\""
            )
        );
    }
}
