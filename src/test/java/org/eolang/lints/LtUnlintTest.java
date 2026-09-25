/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import fixtures.EoProgram;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.cactoos.io.InputOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link LtUnlint}.
 *
 * @since 0.0.59
 */
final class LtUnlintTest {

    @Test
    void keepsAllDefectsWithoutUnlintMeta() throws IOException {
        MatcherAssert.assertThat(
            "All defects must survive when no +unlint meta targets this lint",
            new LtUnlint(new LtUnlintTest.LtFake("foo", 3, 7)).defects(
                LtUnlintTest.program()
            ),
            Matchers.hasSize(2)
        );
    }

    @Test
    void suppressesAllDefectsWithGlobalUnlint() throws IOException {
        MatcherAssert.assertThat(
            "A bare +unlint must clear every defect of that lint",
            new LtUnlint(new LtUnlintTest.LtFake("foo", 3, 7)).defects(
                LtUnlintTest.program("+unlint foo")
            ),
            Matchers.empty()
        );
    }

    @Test
    void suppressesOnlyTheGivenLine() throws IOException {
        MatcherAssert.assertThat(
            "Only line 3 must be suppressed, line 7 must remain",
            LtUnlintTest.lines(
                new LtUnlint(new LtUnlintTest.LtFake("foo", 3, 7)).defects(
                    LtUnlintTest.program("+unlint foo:3")
                )
            ),
            Matchers.contains(7)
        );
    }

    @Test
    void suppressesLinesInsideRangeOnly() throws IOException {
        MatcherAssert.assertThat(
            "Only defects inside the range must be suppressed",
            LtUnlintTest.lines(
                new LtUnlint(new LtUnlintTest.LtFake("foo", 3, 7, 12)).defects(
                    LtUnlintTest.program("+unlint foo:1-8")
                )
            ),
            Matchers.contains(12)
        );
    }

    @Test
    void keepsUnrelatedLintUntouched() throws IOException {
        MatcherAssert.assertThat(
            "A +unlint for another lint name must not affect this one",
            new LtUnlint(new LtUnlintTest.LtFake("foo", 5)).defects(
                LtUnlintTest.program("+unlint bar:5")
            ),
            Matchers.hasSize(1)
        );
    }

    @Test
    void keepsDefectAtLineZeroWhenNotInRange() throws IOException {
        MatcherAssert.assertThat(
            "A line-0 defect not targeted by the range must still be reported",
            LtUnlintTest.lines(
                new LtUnlint(new LtUnlintTest.LtFake("foo", 0, 5)).defects(
                    LtUnlintTest.program("+unlint foo:5")
                )
            ),
            Matchers.contains(0)
        );
    }

    @Test
    void combinesMultipleUnlintMetas() throws IOException {
        MatcherAssert.assertThat(
            "Line 2 and line 9 must be suppressed, line 6 must remain",
            LtUnlintTest.lines(
                new LtUnlint(new LtUnlintTest.LtFake("foo", 2, 6, 9)).defects(
                    LtUnlintTest.program("+unlint foo:2", "+unlint foo:8-10")
                )
            ),
            Matchers.contains(6)
        );
    }

    @Test
    void ignoresDefectsReportedUnderAnotherRuleName() throws IOException {
        MatcherAssert.assertThat(
            "A defect whose rule differs from this lint's own must not be treated as problematic",
            LtUnlintTest.lines(
                new LtUnlint(new LtUnlintTest.MixedRuleFake()).defects(
                    LtUnlintTest.program()
                )
            ),
            Matchers.contains(5)
        );
    }

    @Test
    void delegatesNameToOrigin() {
        MatcherAssert.assertThat(
            "Name must be delegated to the wrapped lint",
            new LtUnlint(new LtUnlintTest.LtFake("origin-name")).name(),
            Matchers.equalTo("origin-name")
        );
    }

    @Test
    void delegatesMotiveToOrigin() throws IOException {
        MatcherAssert.assertThat(
            "Motive must be delegated to the wrapped lint",
            new LtUnlint(new LtUnlintTest.LtFake("foo")).motive(),
            Matchers.equalTo("fake motive")
        );
    }

    @Test
    void delegatesFixToOrigin() {
        MatcherAssert.assertThat(
            "Fix must be delegated to the wrapped lint",
            new LtUnlint(new LtUnlintTest.LtFake("foo")).fix(),
            Matchers.instanceOf(FxEmpty.class)
        );
    }

    private static XML program(final String... unlints) {
        final List<String> src = new ArrayList<>(6);
        src.add("+spdx SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com");
        src.add("+spdx SPDX-License-Identifier: MIT");
        src.addAll(Arrays.asList(unlints));
        src.add("");
        src.add("# Foo.");
        src.add("[] > foo");
        final String txt = String.join(String.valueOf('\n'), src);
        return new EoProgram(txt, new InputOf(txt)).parse();
    }

    private static List<Integer> lines(final Collection<Defect> defects) {
        return defects.stream().map(Defect::line).collect(Collectors.toList());
    }

    /**
     * Fake lint with fixed defects at the given lines.
     *
     * @since 0.0.59
     */
    private static final class LtFake implements Lint {

        /**
         * The lint name.
         */
        private final String lname;

        /**
         * Lines to report a defect at.
         */
        private final int[] lines;

        /**
         * Ctor.
         *
         * @param nme The lint name
         * @param lns Lines to report defects at
         */
        LtFake(final String nme, final int... lns) {
            this.lname = nme;
            this.lines = lns.clone();
        }

        @Override
        public String name() {
            return this.lname;
        }

        @Override
        public Collection<Defect> defects(final XML xmir) {
            return Arrays.stream(this.lines).mapToObj(
                line -> new Defect.Default(this.lname, Severity.WARNING, line, "fake defect")
            ).collect(Collectors.toList());
        }

        @Override
        public String motive() {
            return "fake motive";
        }

        @Override
        public Fix fix() {
            return new FxEmpty();
        }
    }

    /**
     * Fake lint named "foo" that (unrealistically, since a well-behaved lint
     * only ever reports defects under its own name) also reports a defect
     * under a different rule name, to prove such a defect is not treated as
     * this lint's own.
     *
     * @since 0.0.59
     */
    private static final class MixedRuleFake implements Lint {

        @Override
        public String name() {
            return "foo";
        }

        @Override
        public Collection<Defect> defects(final XML xmir) {
            return Arrays.asList(
                new Defect.Default("foo", Severity.WARNING, 5, "own defect"),
                new Defect.Default("other", Severity.WARNING, 8, "foreign defect")
            );
        }

        @Override
        public String motive() {
            return "fake motive";
        }

        @Override
        public Fix fix() {
            return new FxEmpty();
        }
    }
}
