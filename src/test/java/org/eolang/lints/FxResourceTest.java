/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import fixtures.FixPack;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link FxResource}.
 * @since 0.2.1
 */
final class FxResourceTest {

    @Test
    void returnsXmirUnchangedWhenResourceAbsent() throws Exception {
        final XML xmir = new XMLDocument("<o/>");
        MatcherAssert.assertThat(
            "XMIR must be returned unchanged when fix resource does not exist",
            new FxResource("org/eolang/fixes/non-existent.xsl").apply(xmir),
            Matchers.equalTo(xmir)
        );
    }

    @Test
    void appliesXslWhenResourceExists() throws Exception {
        final FixPack pack = new FixPack(
            new TextOf(
                new ResourceOf(
                    "org/eolang/lints/fixes/unsorted-metas/sorts-version-and-spdx.yaml"
                )
            ).asString()
        );
        MatcherAssert.assertThat(
            "Fixed XMIR must match expected after applying fix from classpath resource",
            pack.fixed(new FxResource("org/eolang/fixes/metas/unsorted-metas.xsl")),
            Matchers.equalTo(pack.expected())
        );
    }
}
