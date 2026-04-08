package org.eolang.lints;

import org.cactoos.list.ListOf;
import org.eolang.parser.Program;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link LtNoExtraTestComments}.
 */
final class LtNoExtraTestCommentsTest {

    @Test
    void catchesExtraComment() {
        final Program program = new Program.ProgramOf(
            "+package foo\n\n" +
            "# This test checks how my object works:\n" +
            "[] +> the-object-works\n"
        );
        final Defect defect = new LtNoExtraTestComments()
            .lint(program)
            .iterator()
            .next();
        MatcherAssert.assertThat(
            defect.reason(),
            Matchers.containsString("duplicates the name")
        );
    }

    @Test
    void allowsUsefulComment() {
        final Program program = new Program.ProgramOf(
            "# @todo #1:30min Implement timeout handling\n" +
            "[] +> test-timeout\n"
        );
        final Collection<Defect> defects = new LtNoExtraTestComments()
            .lint(program);
        MatcherAssert.assertThat(defects, Matchers.empty());
    }

    @Test
    void ignoresNonTestObjects() {
        final Program program = new Program.ProgramOf(
            "# This is just a helper object\n" +
            "[x] +> calculator\n"
        );
        final Collection<Defect> defects = new LtNoExtraTestComments()
            .lint(program);
        MatcherAssert.assertThat(defects, Matchers.empty());
    }
}
