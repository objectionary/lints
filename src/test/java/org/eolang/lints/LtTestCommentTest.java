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
    void returnsNoDefectWhenTestObjectHasNoComment() throws IOException {
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
        MatcherAssert.assertThat(defects, Matchers.empty());
    }

    @Test
    void returnsNoDefectWhenNoTestObjectPresent() throws IOException {
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
        MatcherAssert.assertThat(defects, Matchers.empty());
    }

    @Test
    void motiveReturnsExpectedString() throws IOException {
        LtTestComment lint = new LtTestComment();
        MatcherAssert.assertThat(
                lint.motive(),
                Matchers.containsString("Comments in test objects are discouraged")
        );
    }

    @Test
    void nameReturnsExpectedValue() {
        LtTestComment lint = new LtTestComment();
        MatcherAssert.assertThat(
                lint.name(),
                Matchers.equalTo("test-has-comment")
        );
    }
}
