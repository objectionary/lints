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
 * Test for {@link LtTestComment}.
 *
 * @since 0.0.1
 */
final class LtTestCommentTest {

    @Test
    void reportsNoDefectsWhenNoCommentPresent() throws IOException {
        final XML xml = new XMLDocument(
            "<program><object name='foo' line='10'><o name='+test' line='12'></o></object></program>"
        );
        final LtTestComment lint = new LtTestComment();
        final Collection<Defect> defects = lint.defects(xml);
        MatcherAssert.assertThat(
            "Should not report defects when test object has no comment",
            defects,
            Matchers.empty()
        );
    }

    @Test
    void reportsNoDefectsWhenNoObjectPresent() throws IOException {
        final XML xml = new XMLDocument(
            "<program><object name='foo' line='10'><o name='bar' line='12'></o></object></program>"
        );
        final LtTestComment lint = new LtTestComment();
        final Collection<Defect> defects = lint.defects(xml);
        MatcherAssert.assertThat(
            "Should not report defects when no test object is present",
            defects,
            Matchers.empty()
        );
    }

    @Test
    void containsGuidanceInMotive() throws IOException {
        final LtTestComment lint = new LtTestComment();
        MatcherAssert.assertThat(
            "Motive must discourage comments in test objects",
            lint.motive(),
            Matchers.containsString("Avoid comments in test objects; use clear test names")
        );
    }

    @Test
    void returnsStableId() {
        final LtTestComment lint = new LtTestComment();
        MatcherAssert.assertThat(
            "Rule id must be 'test-has-comment'",
            lint.name(),
            Matchers.equalTo("test-has-comment")
        );
    }

}
