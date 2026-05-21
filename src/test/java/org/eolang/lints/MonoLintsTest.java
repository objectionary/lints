/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.log.Logger;
import com.jcabi.xml.XML;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import fixtures.EoProgram;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;
import org.cactoos.list.ListOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link MonoLints}.
 * @since 0.0.43
 */
final class MonoLintsTest {

    @Test
    @SuppressWarnings("JTCOP.RuleAssertionMessage")
    void staysPackagePrivate() {
        ArchRuleDefinition.classes()
            .that().haveSimpleName("MonoLints")
            .should().bePackagePrivate().check(
                new ClassFileImporter()
                    .withImportOption(new ImportOption.DoNotIncludeTests())
                    .importPackages("org.eolang.lints")
            );
    }

    @Test
    void createsLintsWithIncorrectUnlintRule() {
        MatcherAssert.assertThat(
            "Lint `incorrect-unlint` is not present in the list, but it should be",
            new ListOf<>(new MonoLints()).stream().map(Lint::name).collect(Collectors.toList()),
            Matchers.hasItem(
                "incorrect-unlint"
            )
        );
    }

    @Test
    @SuppressWarnings("PMD.UnnecessaryLocalRule")
    void lintsProgramCorrectly() throws IOException {
        final XML xmir = MonoLintsTest.parse();
        final Collection<Defect> found = new ListOf<>();
        new ListOf<>(new MonoLints()).forEach(
            lint -> MonoLintsTest.collect(lint, xmir, found)
        );
        MatcherAssert.assertThat(
            "Found defects should be all unique",
            new HashSet<>(found).size() == found.size(),
            Matchers.equalTo(true)
        );
    }

    /**
     * Parse EO source into XMIR once.
     * @return Parsed XMIR
     */
    private static XML parse() {
        return new EoProgram("org/eolang/lints/simple.eo").parse();
    }

    /**
     * Collect defects from the lint into the accumulator.
     * @param lint Lint to run
     * @param xmir Parsed XMIR
     * @param into Accumulator
     */
    @SuppressWarnings("PMD.UnnecessaryLocalRule")
    private static void collect(
        final Lint lint, final XML xmir, final Collection<Defect> into
    ) {
        try {
            final long start = System.currentTimeMillis();
            into.addAll(lint.defects(xmir));
            Logger.info(
                MonoLintsTest.class,
                "Lint '%s': %dms",
                lint.name(),
                System.currentTimeMillis() - start
            );
        } catch (final IOException exception) {
            throw new IllegalStateException("Failed to lint XMIR", exception);
        }
    }
}
