/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.io.IOException;
import java.util.Collection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link LtRedundantHat}.
 *
 * @since 0.0.60
 */
final class LtRedundantHatTest {

    @Test
    void reportsNoDefectsForNecessaryHat() throws IOException {
        final XML xml = new XMLDocument(
                        "<program><object name='foo' line='10'><o name='+test' line='12'></o></object></program>"
        );
        final LtRedundantHat lint = new LtRedundantHat();
        final Collection<Defect> defects = lint.defects(xml);
        MatcherAssert.assertThat(
                "Should not report a defect when '^' is necessary to resolve ambiguity",
                defects,
                Matchers.empty()
        );
    }

    @Test
    void containsGuidanceInMotive() throws IOException {
        final LtRedundantHat lint = new LtRedundantHat();
        MatcherAssert.assertThat(
                "Motive must explain when to omit redundant '^'",
                lint.motive(),
                Matchers.containsString("notation is used when there are no naming conflicts")
        );
    }

    @Test
    void namesStableId() {
        final LtRedundantHat lint = new LtRedundantHat();
        MatcherAssert.assertThat(
                "Rule id must be 'redundant-hat'",
                lint.name(),
                Matchers.equalTo("redundant-hat")
        );
    }
}
