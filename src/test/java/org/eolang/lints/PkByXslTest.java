/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.log.Logger;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import fixtures.EoProgram;
import fixtures.FixPack;
import io.github.secretx33.resourceresolver.PathMatchingResourcePatternResolver;
import io.github.secretx33.resourceresolver.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.function.Predicate;
import org.cactoos.io.InputOf;
import org.cactoos.io.ResourceOf;
import org.cactoos.list.ListOf;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link PkByXsl}.
 * @since 0.0.1
 */
final class PkByXslTest {

    @Test
    @SuppressWarnings("PMD.UnnecessaryLocalRule")
    void wiresFixForLintWithCorrespondingFixXsl() throws Exception {
        final FixPack pack = new FixPack(
            new TextOf(
                new ResourceOf(
                    "org/eolang/lints/fixes/unsorted-metas/sorts-version-and-spdx.yaml"
                )
            ).asString()
        );
        final Lint lint = new ListOf<>(new PkByXsl()).stream()
            .filter(l -> "unsorted-metas".equals(l.name()))
            .findFirst()
            .orElseThrow();
        MatcherAssert.assertThat(
            "PkByXsl must wire up the fix for lints that have a corresponding fix XSL",
            pack.fixed(lint.fix()),
            Matchers.equalTo(pack.expected())
        );
    }

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
    @SuppressWarnings("PMD.UnnecessaryLocalRule")
    void doesNotDuplicateDefectsWhenMultipleDefectsOnTheSameLine() {
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
     */
    private static XML parse() {
        return new EoProgram("org/eolang/lints/unused-voids.eo").parse();
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
