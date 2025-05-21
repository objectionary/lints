/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
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
import org.cactoos.io.InputOf;
import org.cactoos.list.ListOf;
import org.cactoos.proc.ForEach;
import org.cactoos.text.TextOf;
import org.eolang.parser.EoSyntax;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link PkByXsl}.
 *
 * @since 0.0.1
 */
final class PkByXslTest {

    @Test
    void passesOnSimpleXmir() throws IOException {
        final XML xmir = new XMLDocument("<object><o name='no-exceptions'/></object>");
        for (final Lint<XML> lint : new PkByXsl()) {
            MatcherAssert.assertThat(
                "passes with no exceptions",
                lint.defects(xmir),
                Matchers.notNullValue()
            );
        }
    }

    @Test
    void checksIdsOfEveryXslStylesheet() throws Exception {
        new ForEach<Resource>(
            res -> {
                final XML sheet = new XMLDocument(
                    new TextOf(new InputOf(res.getInputStream())).asString()
                );
                MatcherAssert.assertThat(
                    "the @id is equal to the basename of the file",
                    sheet.xpath("/xsl:stylesheet/@id").get(0),
                    Matchers.equalTo(res.getFilename().replaceAll(".xsl$", ""))
                );
            }
        ).exec(
            Arrays.asList(
                new PathMatchingResourcePatternResolver().getResources(
                    "classpath*:org/eolang/lints/**/*.xsl"
                )
            )
        );
    }

    @Test
    void checksAllMotives() throws Exception {
        for (final Lint<XML> lint : new PkByXsl()) {
            MatcherAssert.assertThat(
                "Lint's motive is empty, but should not be",
                lint.motive().isEmpty(),
                Matchers.equalTo(false)
            );
        }
    }

    @Test
    void doesNotDuplicateDefectsWhenMultipleDefectsOnTheSameLine() throws Exception {
        final XML tuple = new EoSyntax(
            String.join(
                "\n",
                "# Foo with unused voids on the same line.",
                "[x y z] > foo"
            )
        ).parsed();
        final Collection<Defect> aggregated = new ListOf<>();
        new PkByXsl().forEach(
            xsl -> {
                try {
                    aggregated.addAll(xsl.defects(tuple));
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

}
