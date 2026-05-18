/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.log.Logger;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import io.github.secretx33.resourceresolver.PathMatchingResourcePatternResolver;
import io.github.secretx33.resourceresolver.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.function.Predicate;
import org.cactoos.io.InputOf;
import org.cactoos.list.ListOf;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;
import org.eolang.parser.EoSyntax;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link PkByXsl}.
 * @since 0.0.1
 */
final class PkByXslTest {

    @Test
    void passesOnSimpleXmir() throws IOException {
        for (final Lint lint : new PkByXsl()) {
            MatcherAssert.assertThat(
                "passes with no exceptions",
                lint.defects(new XMLDocument("<object><o name='no-exceptions'/></object>")),
                Matchers.notNullValue()
            );
        }
    }

    @Test
    void checksIdsOfEveryXslStylesheet() throws Exception {
        MatcherAssert.assertThat(
            "All XSL stylesheets must have @id matching their filename",
            Arrays.stream(
                new PathMatchingResourcePatternResolver().getResources(
                    "classpath*:org/eolang/lints/**/*.xsl"
                )
            ).allMatch(new PkByXslTest.IdChecker()),
            Matchers.equalTo(true)
        );
    }

    @Test
    void checksAllMotives() throws Exception {
        for (final Lint lint : new PkByXsl()) {
            MatcherAssert.assertThat(
                "Lint's motive is empty, but should not be",
                lint.motive().isEmpty(),
                Matchers.equalTo(false)
            );
        }
    }

    @Test
    void doesNotDuplicateDefectsWhenMultipleDefectsOnTheSameLine() throws Exception {
        final XML xmir = PkByXslTest.parse();
        final Collection<Defect> aggregated = new ListOf<>();
        new PkByXsl().forEach(
            xsl -> {
                try {
                    final long start = System.currentTimeMillis();
                    aggregated.addAll(xsl.defects(xmir));
                    Logger.info(
                        PkByXslTest.class,
                        "Lint '%s': %dms",
                        xsl.name(),
                        System.currentTimeMillis() - start
                    );
                } catch (final IOException exception) {
                    throw new IllegalStateException(
                        String.format("Failed to lint tuple with '%s' lint", xsl.name()),
                        exception
                    );
                }
            }
        );
        MatcherAssert.assertThat(
            Logger.format(
                "Aggregated defects (%[list]s) contain duplicates, but they should not",
                aggregated
            ),
            new HashSet<>(aggregated).size() == aggregated.size(),
            Matchers.equalTo(true)
        );
    }

    /**
     * Parse EO source into XMIR once.
     * @return Parsed XMIR
     * @throws IOException If fails
     */
    private static XML parse() throws IOException {
        final long start = System.currentTimeMillis();
        final XML xmir = new EoSyntax(
            String.join(
                System.lineSeparator(),
                "# Foo with unused voids on the same line.",
                "[x y z] > foo"
            )
        ).parsed();
        Logger.info(
            PkByXslTest.class,
            "Parsing: %dms",
            System.currentTimeMillis() - start
        );
        return xmir;
    }

    /**
     * Checks that XSL stylesheet ID matches filename.
     * @since 0.0.1
     */
    private static final class IdChecker implements Predicate<Resource> {

        @Override
        public boolean test(final Resource res) {
            try {
                return new XMLDocument(
                    new UncheckedText(new TextOf(new InputOf(res.getInputStream()))).asString()
                ).xpath("/xsl:stylesheet/@id").get(0).equals(
                    res.getFilename().replaceAll(".xsl$", "")
                );
            } catch (final IOException ex) {
                throw new IllegalStateException(ex);
            }
        }
    }
}
