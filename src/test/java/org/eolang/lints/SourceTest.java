/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
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
 *
 * @since 0.0.1
 * @checkstyle MethodBodyCommentsCheck (50 lines)
 */
@SuppressWarnings("PMD.TooManyMethods")
@ExtendWith(MktmpResolver.class)
final class SourceTest {

    @Test
    void returnsEmptyListOfDefects() throws IOException {
        MatcherAssert.assertThat(
            "defects found even though the code is clean",
            new Source(
                new EoSyntax(
                    String.join(
                        "\n",
                        "+home https://www.eolang.org",
                        "+package com.example",
                        "+version 0.0.0",
                        // REUSE-IgnoreStart
                        "+spdx SPDX-FileCopyrightText Copyright (c) 2016-2025 Objectionary.com",
                        "+spdx SPDX-License-Identifier: MIT",
                        // REUSE-IgnoreEnd
                        "",
                        "# This is just a test object with no functionality.",
                        "[i] > foo",
                        "  i > @"
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
                            "\n",
                            "+unlint unlint-non-existing-defect",
                            "+unlint empty-object",
                            "+unlint mandatory-home",
                            "+unlint mandatory-package",
                            "+unlint mandatory-version",
                            "+unlint comment-too-short",
                            "+unlint unsorted-metas",
                            "+unlint mandatory-spdx",
                            "+unlint no-attribute-formation",
                            "# Test.",
                            "[] > foo"
                        )
                    )
                ).parsed()
            ).defects(),
            Matchers.emptyIterable()
        );
    }

    @Test
    @Timeout(60L)
    void checksSimple(@Mktmp final Path dir) throws IOException {
        final Path path = dir.resolve("foo.xmir");
        Files.write(
            path,
            new EoSyntax(
                "# first.\n[] > foo\n# second.\n[] > foo\n"
            ).parsed().toString().getBytes(StandardCharsets.UTF_8)
        );
        MatcherAssert.assertThat(
            "the defect is found",
            new Source(path).defects().size(),
            Matchers.greaterThan(0)
        );
    }

    @Tag("deep")
    @Timeout(60L)
    @RepeatedTest(2)
    void lintsInMultipleThreads() {
        MatcherAssert.assertThat(
            "wrong number of defects found, in parallel",
            new SetOf<>(
                new Together<>(
                    t -> new Source(
                        new EoSyntax(
                            "# first.\n[] > foo\n"
                        ).parsed()
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
                            "\n",
                            "+version 8.8.8-beta",
                            "+alias org.eolang.txt.sprintf",
                            "+alias org . eolang . txt . broken",
                            "+version 1.1-another maybe be wrong",
                            "+package Z.Y.Z",
                            "+home some-wrong-URL",
                            "+architect broken-email-here",
                            "",
                            "# комментарий здесь",
                            "[] > foo-bar",
                            "  boom > @",
                            "    bar 42 > zzz"
                        )
                    )
                ).parsed()
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
                    new XMLDocument("<object><o name='correct'/></object>")
                ).defects(),
            "Exception was thrown, but it should not be"
        );
    }

    @Test
    void createsSourceWithoutOneLint() throws IOException {
        final String disabled = "ascii-only";
        MatcherAssert.assertThat(
            "Defects for disabled lint are not empty, but should be",
            new Source(
                new EoSyntax(
                    "# привет\n# как дела?\n[] > foo\n"
                ).parsed()
            ).without(disabled).defects().stream()
                .filter(defect -> defect.rule().equals(disabled))
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
                    "# привет\n# как дела?\n[] > foo\n"
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
                "no-attribute-formation"
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
                        "\n",
                        "+home https://github.com/objectionary/eo",
                        "+package f",
                        "+version 0.0.0",
                        "",
                        "# No comments.",
                        "[c] > main",
                        "  QQ.io.stdout > @",
                        "    QQ.txt.sprintf",
                        "      \"Hello %s\"",
                        "      * c"
                    )
                ).parsed()
            ).without("mandatory-spdx").defects(),
            Matchers.hasSize(1)
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
                        "\n",
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
                "no-attribute-formation"
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
                    "\n",
                    "# Foo with unused voids on the same line.",
                    "[x y z] > foo"
                )
            ).parsed()
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
                        "\n",
                        "# Foo",
                        "[] > foo"
                    )
                ).parsed()
            ).defects(),
            Matchers.hasItem(
                Matchers.hasToString(
                    Matchers.allOf(
                        Matchers.containsString("comment-without-dot (Single) WARNING"),
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
        result.keySet().forEach(
            defects -> {
                final SourceSize source = defects.keySet().iterator().next();
                MatcherAssert.assertThat(
                    String.format(
                        "Defects for source '%s' are empty, but they should not be",
                        source.java()
                    ),
                    defects.get(source),
                    Matchers.hasSize(Matchers.greaterThan(0))
                );
            }
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
        final Pattern tpattern = Pattern.compile(
            "^Lint time: (\\d+(?:\\.\\d+)?)(ms|s|min|h) \\(\\d+ ms\\)$"
        );
        final Pattern nlines = Pattern.compile("\\R");
        Arrays.stream(
            nlines.split(SourceTest.benchmarkResults().values().iterator().next())
            )
            .filter(line -> line.startsWith("Lint time:"))
            .forEach(
                text ->
                    MatcherAssert.assertThat(
                        String.format(
                            "Lint time '%s' does not match '%s' regex, but it should",
                            text, tpattern
                        ),
                        tpattern.matcher(text).matches(),
                        Matchers.equalTo(true)
                    )
            );
    }

    @Test
    void checksJavaSourcesForBenchmarking() {
        new ListOf<>(SourceSize.values()).forEach(
            src -> {
                final LineCountVisitor visitor = new LineCountVisitor();
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
                final int min = src.minAllowed();
                final int max = src.maxAllowed();
                MatcherAssert.assertThat(
                    String.join(
                        ", ",
                        String.format(
                            "Java source \"%s\" was supplied with incorrect size marker (\"%s\")",
                            src, src.type()
                        ),
                        String.format(
                            "since it has %d executable lines inside",
                            lines
                        ),
                        String.format(
                            "while it is expected to have between %d and %d line numbers",
                            min, max
                        )
                    ),
                    lines,
                    Matchers.allOf(
                        Matchers.greaterThanOrEqualTo(min),
                        Matchers.lessThanOrEqualTo(max)
                    )
                );
            }
        );
    }

    /**
     * Run benchmark, and output the results.
     * @return Benchmark results
     */
    private static Map<Map<SourceSize, Collection<Defect>>, String> benchmarkResults() {
        final List<Map<SourceSize, Collection<Defect>>> runs = new ListOf<>();
        final StringBuilder sum = new StringBuilder();
        new ListOf<>(SourceSize.values()).forEach(
            source -> {
                final XML xmir = new Unchecked<>(new BytecodeClass(source)).value();
                final long start = System.currentTimeMillis();
                final Collection<Defect> defects = new BcSource(
                    xmir, source.type()
                ).defects();
                final long msec = System.currentTimeMillis() - start;
                runs.add(new MapOf<>(source, defects));
                sum.append(
                    String.join(
                        "\n",
                        String.format(
                            "Input: %s (%s source)", source.java(), source.type()
                        ),
                        Logger.format(
                            "Lint time: %[ms]s (%d ms)",
                            msec, msec
                        )
                    )
                ).append("\n\n");
            }
        );
        return runs.stream().collect(
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
        private final Iterable<Lint<XML>> lints;

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
            final XML source, final Iterable<Lint<XML>> lnts, final Tojos tmngs, final String size
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
        public Collection<Defect> defects() {
            try {
                final Collection<Defect> messages = new ArrayList<>(0);
                for (final Lint<XML> lint : this.lints) {
                    final long start = System.currentTimeMillis();
                    final Collection<Defect> defects = lint.defects(this.xmir);
                    final long done = System.currentTimeMillis() - start;
                    this.timings.add(String.format("%s (%s)", lint.name(), this.marker))
                        .set("ms", done);
                    messages.addAll(defects);
                }
                return messages;
            } catch (final IOException ex) {
                throw new IllegalStateException(
                    "Failed to find defects in the XMIR file",
                    ex
                );
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
        public int total() {
            return this.count;
        }
    }
}
