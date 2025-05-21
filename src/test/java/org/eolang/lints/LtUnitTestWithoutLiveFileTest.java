/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import matchers.DefectMatcher;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link LtUnitTestWithoutLiveFile}.
 *
 * @since 0.0.30
 */
final class LtUnitTestWithoutLiveFileTest {

    @Test
    void catchesAbsenceOfLiveFile() {
        MatcherAssert.assertThat(
            "Defects are empty, but they should not",
            new LtUnitTestWithoutLiveFile().defects(
                new MapOf<String, XML>(
                    new MapEntry<>("abc-test", new XMLDocument("<object/>")),
                    new MapEntry<>("cde", new XMLDocument("<object/>"))
                )
            ),
            Matchers.hasSize(Matchers.greaterThan(0))
        );
    }

    @Test
    void reportsProperly() {
        MatcherAssert.assertThat(
            "Defects are empty, but they should not",
            new LtUnitTestWithoutLiveFile().defects(
                new MapOf<String, XML>(
                    new MapEntry<>(
                        "xyz-test", new XMLDocument("<object><o name='xyz-test'/></object>")
                    ),
                    new MapEntry<>(
                        "bar", new XMLDocument("<object><o name='bar'/></object>")
                    )
                )
            ),
            Matchers.everyItem(new DefectMatcher())
        );
    }

    @Test
    void acceptsValidPackage() {
        MatcherAssert.assertThat(
            "Defects are not empty, but they should",
            new LtUnitTestWithoutLiveFile().defects(
                new MapOf<String, XML>(
                    new MapEntry<>(
                        "foo-test", new XMLDocument("<object><o name='foo-test'/></object>")
                    ),
                    new MapEntry<>("foo", new XMLDocument("<object><o name='foo'/></object>"))
                )
            ),
            Matchers.emptyIterable()
        );
    }
}
