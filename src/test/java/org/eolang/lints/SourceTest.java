/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.log.Logger;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import com.yegor256.MayBeSlow;
import com.yegor256.Mktmp;
import com.yegor256.MktmpResolver;
import com.yegor256.Together;
import com.yegor256.tojos.MnCsv;
import com.yegor256.tojos.TjCached;
import com.yegor256.tojos.TjDefault;
import com.yegor256.tojos.Tojos;
import fixtures.BytecodeClass;
import fixtures.SourceSize;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.cactoos.bytes.BytesOf;
import org.cactoos.bytes.UncheckedBytes;
import org.cactoos.io.InputOf;
import org.cactoos.io.ResourceOf;
import org.cactoos.iterable.Sticky;
import org.cactoos.iterable.Synced;
import org.cactoos.list.ListOf;
import org.cactoos.map.MapOf;
import org.cactoos.scalar.Unchecked;
import org.cactoos.set.SetOf;
import org.eolang.parser.EoSyntax;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Test for {@link Source}.
 * @since 0.0.1
 * @todo #767:60min Decrease timeouts for source linting.
 *  As for now, most lints are too slow, we need to optimize them first, so they
 *  run in milliseconds, not seconds/minutes. It should decrease our build time too.
 *  After that, we need to decrease our test timeouts. Don't forget to remove this puzzle.
 * @checkstyle MethodBodyCommentsCheck (50 lines)
 * @checkstyle ClassFanOutComplexityCheck (500 lines)
 */
@SuppressWarnings("PMD.TooManyMethods")
@ExtendWith(MktmpResolver.class)
final class SourceTest {

    @Timeout(unit = TimeUnit.SECONDS, value = 60L)
    @Test
    void returnsEmptyListOfDefects() throws IOException {
        MatcherAssert.assertThat(
            "defects found even though the code is clean",
            new Source(
                new EoSyntax(
                    String.join(
                        System.lineSeparator(),
                        "+home https://www.eolang.org",
                        "+package com.example",
                        "+version 0.0.0",
                        // REUSE-IgnoreStart
                        "+spdx SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com",
                        "+spdx SPDX-License-Identifier: MIT",
                        // REUSE-IgnoreEnd,
                        "",
                        "# This is just a test object with no functionality.",
                        "[i] > foo",
                        "  i > @",
                        "  # This is real unit test for object with no functionality.",
                        "  [] +> does-something",
                        "    foo 1 > @"
                    )
                ).parsed()
            ).defects(),
            Matchers.emptyIterable()
        );
    }

    @Test
    void suppressesManyLints() throws IOException {
        MatcherAssert.assertThat(
            "defect found even though lint is suppressed",
            new Source(
                new EoSyntax(
                    new InputOf(
                        String.join(
                            System.lineSeparator(),
                            "+unlint unlint-non-existing-defect",
                            "+unlint empty-object",
                            "+unlint mandatory-home",
                            "+unlint mandatory-package",
                            "+unlint mandatory-version",
                            "+unlint comment-too-short",
                            "+unlint unsorted-metas",
                            "+unlint mandatory-spdx",
                            "+unlint no-attribute-formation",
                            "+unlint unit-test-missing",
                            "",
                            "# Test.",
                            "[] > one"
                        )
                    )
                ).parsed()
            ).defects(),
            Matchers.emptyIterable()
        );
    }

    @Test
    void checksSimple(@Mktmp final Path dir) throws IOException {
        final Path path = dir.resolve("foo.xmir");
        Files.write(
            path,
            new EoSyntax(
                String.join(
                    System.lineSeparator(),
                    "# Foo",
                    "[] > two",
                    ""
                )
            ).parsed().toString().getBytes(StandardCharsets.UTF_8)
        );
        MatcherAssert.assertThat(
            "the defect is found",
            new Source(
                new XMLDocument(path), new ListOf<>(new LtByXsl("comments/comment-without-dot"))
            ).defects().size(),
            Matchers.greaterThan(0)
        );
    }

    @Tag("deep")
    @RepeatedTest(2)
    void lintsInMultipleThreads() {
        MatcherAssert.assertThat(
            "wrong number of defects found, in parallel",
            new SetOf<>(
                new Together<>(
                    t -> new Source(
                        new EoSyntax(
                            String.join(
                                System.lineSeparator(),
                                "# Foo",
                                "[] > three",
                                ""
                            )
                        ).parsed(),
                        new ListOf<>(new LtByXsl("comments/comment-without-dot"))
                    ).defects().size()
                ).asList()
            ).size(),
            Matchers.equalTo(1)
        );
    }

    @Test
    void checksLargerBrokenSource() throws IOException {
        MatcherAssert.assertThat(
            "checking passes",
            new Source(
                new EoSyntax(
                    new InputOf(
                        String.join(
                            System.lineSeparator(),
                            "+version 8.8.8-beta",
                            "+alias org.eolang.txt.sprintf",
                            "+alias org . eolang . txt . broken",
                            "+version 1.1-another maybe be wrong",
                            "+package Z.Y.Z",
                            "+home some-wrong-URL",
                            "+architect broken-email-here",
                            "",
                            "# A comment here.",
                            "[] > foo-bar",
                            "  boom > @",
                            "    bar 42 > zzz"
                        )
                    )
                ).parsed(),
                new ListOf<>(new LtByXsl("aliases/alias-too-long"))
            ).defects(),
            Matchers.allOf(
                Matchers.iterableWithSize(Matchers.greaterThan(0)),
                Matchers.hasItem(
                    Matchers.hasToString(
                        Matchers.allOf(
                            Matchers.containsString("alias-too-long"),
                            Matchers.containsString("The alias has too many parts"),
                            Matchers.containsString(":3")
                        )
                    )
                )
            )
        );
    }

    @Test
    @Timeout(60L)
    void acceptsCanonicalCode() throws IOException {
        final XML xmir = new EoSyntax(
            new ResourceOf(
                "org/eolang/lints/canonical.eo"
            )
        ).parsed();
        MatcherAssert.assertThat(
            String.format("no errors in canonical code in %s", xmir),
            new Source(xmir).defects(),
            Matchers.emptyIterable()
        );
    }

    @Test
    void doesNotThrowIoException() {
        Assertions.assertDoesNotThrow(
            () ->
                new Source(
                    new XMLDocument("<object><o name='correct'/></object>"),
                    new ListOf<>(new LtByXsl("comments/comment-without-dot"))
                ).defects(),
            "Exception was thrown, but it should not be"
        );
    }

    @Test
    void createsSourceWithoutOneLint() throws IOException {
        MatcherAssert.assertThat(
            "Defects for disabled lint are not empty, but should be",
            new Source(
                new EoSyntax(
                    String.join(
                        System.lineSeparator(),
                        "# привет",
                        "# как дела?",
                        "[] > four",
                        ""
                    )
                ).parsed()
            ).without("ascii-only").defects().stream()
                .filter(defect -> defect.rule().equals("ascii-only"))
                .collect(Collectors.toList()),
            Matchers.emptyIterable()
        );
    }

    @Test
    void createsSourceWithoutMultipleLints() throws IOException {
        MatcherAssert.assertThat(
            "Defects for disabled lints are not empty, but should be",
            new Source(
                new EoSyntax(
                    String.join(
                        System.lineSeparator(),
                        "# привет",
                        "# как дела?",
                        "[] > five",
                        ""
                    )
                ).parsed()
            ).without(
                "ascii-only",
                "object-does-not-match-filename",
                "comment-not-capitalized",
                "empty-object",
                "mandatory-home",
                "mandatory-version",
                "mandatory-package",
                "comment-too-short",
                "mandatory-spdx",
                "no-attribute-formation",
                "unit-test-missing"
            ).defects(),
            Matchers.emptyIterable()
        );
    }

    @Test
    void returnsOnlyOneDefect() throws IOException {
        MatcherAssert.assertThat(
            "Only one defect should be found",
            new Source(
                new EoSyntax(
                    String.join(
                        System.lineSeparator(),
                        "+home https://github.com/objectionary/eo",
                        "+package f",
                        "+version 0.0.0",
                        "",
                        "# No comments.",
                        "[c] > main",
                        "  QQ.io.stdout > @",
                        "    QQ.txt.sprintf",
                        "      \"Hello %s\"",
                        "      * c",
                        "  [] +> starts-correctly",
                        "    main 42 > @"
                    )
                ).parsed()
            ).without("mandatory-spdx").defects(),
            Matchers.hasSize(1)
        );
    }

    @Test
    void disablesUnlintNonExistingDefectViaWithout() throws IOException {
        MatcherAssert.assertThat(
            "unlint-non-existing-defect should be silenced when disabled via without()",
            new Source(
                new EoSyntax(
                    String.join(
                        System.lineSeparator(),
                        "+unlint mandatory-home",
                        "",
                        "# Foo.",
                        "[] > foo"
                    )
                ).parsed()
            ).without(
                "unlint-non-existing-defect",
                "mandatory-home",
                "mandatory-version",
                "empty-object",
                "mandatory-package",
                "mandatory-spdx",
                "comment-too-short",
                "no-attribute-formation",
                "unit-test-missing"
            ).defects().stream()
                .filter(defect -> defect.rule().startsWith("unlint-non-existing-defect"))
                .collect(Collectors.toList()),
            Matchers.emptyIterable()
        );
    }

    @ParameterizedTest
    @ValueSource(
        strings = {"mandatory-home", "mandatory-home:0"}
    )
    void catchesBrokenUnlintAfterLintWasRemoved(final String lid) throws IOException {
        MatcherAssert.assertThat(
            "Found defect does not match with expected",
            new Source(
                new EoSyntax(
                    String.join(
                        System.lineSeparator(),
                        String.format("+unlint %s", lid),
                        "",
                        "# Foo.",
                        "[] > foo"
                    )
                ).parsed()
            ).without(
                "mandatory-home",
                "mandatory-version",
                "empty-object",
                "mandatory-package",
                "mandatory-spdx",
                "comment-too-short",
                "no-attribute-formation",
                "unit-test-missing"
            ).defects(),
            Matchers.allOf(
                Matchers.iterableWithSize(1),
                Matchers.hasItem(
                    Matchers.hasToString(
                        Matchers.allOf(
                            Matchers.containsString("unlint-non-existing-defect"),
                            Matchers.containsString(
                                String.format("Unlinting rule '%s' doesn't make sense,", lid)
                            ),
                            Matchers.containsString("since there are no defects with it")
                        )
                    )
                )
            )
        );
    }

    @Test
    void doesNotDuplicateDefectsWhenMultipleDefectsOnTheSameLine() throws IOException {
        final Collection<Defect> defects = new Source(
            new EoSyntax(
                String.join(
                    System.lineSeparator(),
                    "# Foo with unused voids on the same line.",
                    "[x y z] > foo"
                )
            ).parsed(),
            new ListOf<>(new LtByXsl("misc/unused-void-attr"))
        ).defects();
        MatcherAssert.assertThat(
            Logger.format(
                "Found defects (%[list]s) contain duplicates, but they should not",
                defects
            ),
            new HashSet<>(defects).size() == defects.size(),
            Matchers.equalTo(true)
        );
    }

    @Test
    void outputsInformationAboutSingleScope() throws IOException {
        MatcherAssert.assertThat(
            "Found defects don't contain information about Single scope, but they should",
            new Source(
                new EoSyntax(
                    String.join(
                        System.lineSeparator(),
                        "# Foo",
                        "[] > foo"
                    )
                ).parsed(),
                new ListOf<>(new LtByXsl("comments/comment-without-dot"))
            ).defects(),
            Matchers.hasItem(
                Matchers.hasToString(
                    Matchers.allOf(
                        Matchers.containsString("comment-without-dot/S WARNING"),
                        Matchers.containsString(":2")
                    )
                )
            )
        );
    }

    @Test
    @Tag("benchmark")
    @ExtendWith(MktmpResolver.class)
    @ExtendWith(MayBeSlow.class)
    @Timeout(600L)
    void lintsBenchmarkSourcesFromJava() throws Exception {
        final Map<Map<SourceSize, Collection<Defect>>, String> result =
            SourceTest.benchmarkResults();
        MatcherAssert.assertThat(
            "All benchmark sources must produce at least one defect",
            result.keySet().stream().allMatch(
                defects -> !defects.get(defects.keySet().iterator().next()).isEmpty()
            ),
            Matchers.equalTo(true)
        );
        Files.write(
            Paths.get("target").resolve("lint-summary.txt"),
            result.values().iterator().next().getBytes(StandardCharsets.UTF_8)
        );
    }

    @Test
    @Tag("benchmark")
    @ExtendWith(MayBeSlow.class)
    @Timeout(600L)
    void checksLintTimeFormattingInBenchmarkResults() {
        MatcherAssert.assertThat(
            "All lint time entries must match the expected format",
            Arrays.stream(
                Pattern.compile("\\R").split(
                    SourceTest.benchmarkResults().values().iterator().next()
                )
            ).filter(line -> line.startsWith("Lint time:")).allMatch(
                text -> Pattern.compile(
                    "^Lint time: (\\d+(?:\\.\\d+)?)(ms|s|min|h) \\(\\d+ ms\\)$"
                ).matcher(text).matches()
            ),
            Matchers.equalTo(true)
        );
    }

    @Test
    void checksJavaSourcesForBenchmarking() {
        MatcherAssert.assertThat(
            "All Java sources must have line counts within expected bounds",
            Arrays.stream(SourceSize.values()).allMatch(SourceTest::lineCountWithinBounds),
            Matchers.equalTo(true)
        );
    }

    private static boolean lineCountWithinBounds(final SourceSize src) {
        final SourceTest.LineCountVisitor visitor = new SourceTest.LineCountVisitor();
        new ClassReader(
            new UncheckedBytes(
                new BytesOf(
                    new ResourceOf(
                        src.java()
                    )
                )
            ).asBytes()
        ).accept(visitor, 0);
        final int lines = visitor.total();
        return lines >= src.minAllowed() && lines <= src.maxAllowed();
    }

    /**
     * Run benchmark, and output the results.
     * @return Benchmark results
     */
    private static Map<Map<SourceSize, Collection<Defect>>, String> benchmarkResults() {
        final List<Map<SourceSize, Collection<Defect>>> results = new ArrayList<>(0);
        final StringBuilder sum = new StringBuilder();
        for (final SourceSize source : SourceSize.values()) {
            final long before = System.currentTimeMillis();
            final Collection<Defect> defects = new SourceTest.BcSource(
                new Unchecked<>(new BytecodeClass(source.name(), source.java())).value(),
                source.type()
            ).defects();
            final long msec = System.currentTimeMillis() - before;
            results.add(new MapOf<>(source, defects));
            sum.append(
                String.join(
                    System.lineSeparator(),
                    String.format(
                        "Input: %s (%s source)", source.java(), source.type()
                    ),
                    Logger.format(
                        "Lint time: %[ms]s (%d ms)",
                        msec, msec
                    )
                )
            ).append(System.lineSeparator()).append(System.lineSeparator());
        }
        return results.stream().collect(
            Collectors.toMap(run -> run, run -> sum.toString())
        );
    }

    /**
     * Benchmarked source.
     * @since 0.0.29
     */
    private static final class BcSource {

        /**
         * XMIR.
         */
        private final XML xmir;

        /**
         * Lints to apply.
         */
        private final Iterable<Lint> lints;

        /**
         * Timings.
         */
        private final Tojos timings;

        /**
         * Size marker of the source.
         */
        private final String marker;

        /**
         * Ctor.
         * @param source XMIR source to lint
         * @param size Source size
         */
        BcSource(final XML source, final String size) {
            this(
                source,
                new Synced<>(new Sticky<>(new PkMono())),
                new TjCached(
                    new TjDefault(
                        new MnCsv("target/timings.csv")
                    )
                ),
                size
            );
        }

        /**
         * Ctor.
         * @param source XMIR source file to lint
         * @param lnts Lints to apply
         * @param tmngs Timings
         * @param size Source size
         * @checkstyle ParameterNumberCheck (5 lines)
         */
        BcSource(
            final XML source, final Iterable<Lint> lnts, final Tojos tmngs, final String size
        ) {
            this.xmir = source;
            this.lints = lnts;
            this.timings = tmngs;
            this.marker = size;
        }

        /**
         * Defects.
         * @return Defects
         * @todo #144:35min Resolve code duplication with Source.java class.
         *  Currently, BcSource.java is duplication of Source.java. Let's make
         *  it use the original Source.java, so they will stay synced. Don't forget
         *  to remove this puzzle.
         */
        Collection<Defect> defects() {
            try {
                final Collection<Defect> messages = new ArrayList<>(0);
                for (final Lint lint : this.lints) {
                    messages.addAll(this.timed(lint));
                }
                return messages;
            } catch (final IOException ex) {
                throw new IllegalStateException(
                    "Failed to find defects in the XMIR file",
                    ex
                );
            }
        }

        /**
         * Execute lint and record timing.
         * @param lint Lint to execute
         * @return Defects found
         * @throws IOException If fails
         */
        private Collection<Defect> timed(final Lint lint) throws IOException {
            return new SourceTest.TimedLint(lint, this.xmir, this.timings, this.marker).defects();
        }
    }

    /**
     * Wrapper for timed lint execution.
     * @since 0.0.45
     */
    private static final class TimedLint {

        /**
         * Lint to execute.
         */
        private final Lint lint;

        /**
         * XML to check.
         */
        private final XML xml;

        /**
         * Timings recorder.
         */
        private final Tojos timings;

        /**
         * Size marker.
         */
        private final String marker;

        /**
         * Ctor.
         * @param lnt Lint
         * @param xmr XML
         * @param tmngs Timings
         * @param mrkr Marker
         * @checkstyle ParameterNumberCheck (5 lines)
         */
        TimedLint(final Lint lnt, final XML xmr, final Tojos tmngs, final String mrkr) {
            this.lint = lnt;
            this.xml = xmr;
            this.timings = tmngs;
            this.marker = mrkr;
        }

        /**
         * Get defects with timing.
         * @return Defects
         * @throws IOException If fails
         */
        @SuppressWarnings("PMD.UnnecessaryLocalRule")
        Collection<Defect> defects() throws IOException {
            final long before = System.currentTimeMillis();
            try {
                return this.lint.defects(this.xml);
            } finally {
                this.timings.add(String.format("%s (%s)", this.lint.name(), this.marker))
                    .set("ms", System.currentTimeMillis() - before);
            }
        }
    }

    /**
     * Line number visitor.
     * Here, we count executable lines from Java bytecode class. However, if compiler
     * decides to skip them, we will get 0 here. Thus, all classes must be compiled with
     * lines.
     * @since 0.0.45
     */
    private static final class LineCountVisitor extends ClassVisitor {

        /**
         * Count.
         */
        private int count;

        /**
         * Ctor.
         */
        LineCountVisitor() {
            super(Opcodes.ASM9);
        }

        @Override
        public MethodVisitor visitMethod(
            final int access,
            final String name,
            final String descriptor,
            final String signature,
            final String[] exceptions
        ) {
            return new MethodVisitor(Opcodes.ASM9) {
                @Override
                public void visitLineNumber(final int line, final Label start) {
                    SourceTest.LineCountVisitor.this.count += 1;
                }
            };
        }

        /**
         * Total found.
         * @return Lines count
         */
        int total() {
            return this.count;
        }
    }
}
