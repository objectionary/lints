/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XMLDocument;
import java.io.IOException;
import org.eolang.parser.EoSyntax;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link LtRedundantRho}.
 *
 * @since 0.0.1
 */
final class LtRedundantRhoTest {

    @Test
    void reportsNoDefectsForNecessaryHat() throws Exception {
        MatcherAssert.assertThat(
            "Should not report a defect when '^' is necessary to resolve ambiguity",
            new LtRedundantRho().defects(
                new EoSyntax("[] > foo\n+test > @\n^foo > bar").parsed()
            ),
            Matchers.allOf(
                Matchers.iterableWithSize(Matchers.equalTo(0))
            )
        );
    }

    @Test
    void namesStableId() {
        MatcherAssert.assertThat(
            "Rule id must be 'redundant-hat'",
            new LtRedundantRho().name(),
            Matchers.equalTo("redundant-hat")
        );
    }

    @Test
    void doesNotReportDefectWhenCaretIsNecessary() throws IOException {
        MatcherAssert.assertThat(
            "Should not report a defect when '^' is necessary due to ambiguity",
            new LtRedundantRho().defects(
                new EoSyntax("[] > foo\n+test > @\n^foo > bar").parsed()
            ),
            Matchers.empty()
        );
    }

    @Test
    void reportsDefectWhenCaretIsRedundantWithMultipleMatches() throws IOException {
        MatcherAssert.assertThat(
            "Should report a defect when '^' is redundant among multiple matches",
            new LtRedundantRho().defects(
                new XMLDocument("<object><o name='foo' base='ξ.ρ' line='12'></o></object>")
            ),
            Matchers.hasSize(1)
        );
    }

}
