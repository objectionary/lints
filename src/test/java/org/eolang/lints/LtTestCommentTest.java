/*

SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
SPDX-License-Identifier: MIT
*/
package org.eolang.lints;

import com.github.lombrozo.xnav.Xnav;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collection;

final class LtTestCommentTest {

    @Test
    void reportsNoDefectsWhenTestObjectHasNoComment() throws IOException {
        XML xml = new XMLDocument(
                "<program>" +
                        "<object name='foo' line='10'>" +
                        "<o name='+test' line='12'>" +
                        "</o>" +
                        "</object>" +
                        "</program>"
        );
        LtTestComment lint = new LtTestComment();
        Collection<Defect> defects = lint.defects(xml);
        MatcherAssert.assertThat(
            "Should not report defects when test object has no comment",
            defects,
            Matchers.empty()
        );
    }

    @Test
    void reportsNoDefectsWhenNoTestObjectPresent() throws IOException {
        XML xml = new XMLDocument(
                "<program>" +
                        "<object name='foo' line='10'>" +
                        "<o name='bar' line='12'>" +
                        "</o>" +
                        "</object>" +
                        "</program>"
        );
        LtTestComment lint = new LtTestComment();
        Collection<Defect> defects = lint.defects(xml);
        MatcherAssert.assertThat(
            "Should not report defects when no test object is present",
            defects,
            Matchers.empty()
        );
    }

    @Test
    void motiveContainsGuidance() throws IOException {
        LtTestComment lint = new LtTestComment();
        MatcherAssert.assertThat(
                "Motive must discourage comments in test objects",
                lint.motive(),
                Matchers.containsString("Comments in test objects are discouraged")
        );
    }

    @Test
    void nameIsStableId() {
        LtTestComment lint = new LtTestComment();
        MatcherAssert.assertThat(
                "Rule id must be 'test-has-comment'",
                lint.name(),
                Matchers.equalTo("test-has-comment")
        );
    }

    @Test
    void reportsDefectWhenTestObjectHasComment() throws IOException {
        final XML xml = new XMLDocument(
            "<program>" +
                "<object name='foo' line='10'>" +
                "<o name='+test' line='12'>" +
                "<meta key='comment' line='13'/>" +
                "</o>" +
                "</object>" +
            "</program>"
        );
        final LtTestComment lint = new LtTestComment();
        final Collection<Defect> defects = lint.defects(xml);
        MatcherAssert.assertThat(
            "Should report exactly one defect when a test object has a comment",
            defects,
            Matchers.hasSize(1)
        );
    }

}
