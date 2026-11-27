/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XMLDocument;
import fixtures.EoProgram;
import org.cactoos.io.InputOf;
import org.cactoos.list.ListOf;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
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

    @Test
    void catchesReservedLocalNameBoundWithDoubleArrow() throws Exception {
        MatcherAssert.assertThat(
            "Local name bound with >> must be checked against reserved names",
            new ListOf<>(
                new LtReservedName(
                    new MapOf<>(new MapEntry<>("true", "true.eo"))
                ).defects(
                    new EoProgram(
                        "reserved-double-arrow",
                        new InputOf(
                            String.join(
                                System.lineSeparator(),
                                "[] > qqq",
                                "  42 >> true"
                            )
                        )
                    ).parse()
                )
            ).get(0).text(),
            Matchers.equalTo(
                "Object name \"true\" is already reserved by object in the \"true.eo\""
            )
        );
    }

    @Test
    void ignoresEmptyNameAttribute() throws Exception {
        MatcherAssert.assertThat(
            "Empty @name attribute must not fail with NoSuchElementException",
            new LtReservedName(
                new MapOf<>(new MapEntry<>("true", "true.eo"))
            ).defects(
                new XMLDocument("<object><o line=\"1\" name=\"\"/></object>")
            ),
            Matchers.empty()
        );
    }
}
