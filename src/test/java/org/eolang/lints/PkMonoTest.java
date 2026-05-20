/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.log.Logger;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import com.yegor256.Together;
import io.github.secretx33.resourceresolver.PathMatchingResourcePatternResolver;
import io.github.secretx33.resourceresolver.Resource;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.cactoos.io.ResourceOf;
import org.cactoos.list.ListOf;
import org.cactoos.scalar.LengthOf;
import org.cactoos.set.SetOf;
import org.eolang.parser.EoSyntax;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link PkMono}.
 * @since 0.23
 */
final class PkMonoTest {

    @Tag("deep")
    @RepeatedTest(5)
    void createsLintsInParallel() {
        MatcherAssert.assertThat(
            "",
            new SetOf<>(
                new Together<>(
                    thread -> new LengthOf(new PkMono()).value()
                )
            ).size(),
            Matchers.equalTo(1)
        );
    }

    @Test
    void allowsUnlint() throws IOException {
        MatcherAssert.assertThat(
            "Defects found, though they were unlinted",
            new Source(
                new EoSyntax(
                    new ResourceOf("org/eolang/lints/unlint-ascii-only.eo")
                ).parsed(),
                new PkMono()
            ).defects().stream().filter(
                defect -> "ascii-only".equals(defect.rule())
            ).collect(Collectors.toList()),
            Matchers.emptyIterable()
        );
    }

    @Test
    void checksThatLintsCanBeUnlinted() {
        MatcherAssert.assertThat(
            "All lints (except LtIncorrectUnlint) must be wrapped by LtUnlint",
            new ListOf<>(new PkMono()).stream().filter(
                lint -> !PkMonoTest.decoratee(lint).getClass().equals(LtIncorrectUnlint.class)
            ).allMatch(
                lint -> PkMonoTest.decoratee(lint).getClass().equals(LtUnlint.class)
            ),
            new IsEqual<>(true)
        );
    }

    @Test
    void linksEveryMotiveFileToALint() throws Exception {
        final List<String> orphans = Arrays.stream(
            new PathMatchingResourcePatternResolver().getResources(
                "classpath*:org/eolang/motives/**/*.md"
            )
        ).map(PkMonoTest::shortName)
            .filter(((Predicate<String>) PkMonoTest.lintNames()::contains).negate())
            .distinct()
            .sorted()
            .collect(Collectors.toList());
        MatcherAssert.assertThat(
            Logger.format(
                "Motive files without a corresponding lint: %[list]s. Each .md under src/main/resources/org/eolang/motives/ must match the name() of a lint produced by PkMono.",
                orphans
            ),
            orphans,
            Matchers.empty()
        );
    }

    @Test
    @SuppressWarnings("JTCOP.RuleAssertionMessage")
    void staysInsideThePackage() {
        ArchRuleDefinition.classes()
            .that().haveSimpleName("PkMono")
            .should().bePackagePrivate().check(
                new ClassFileImporter()
                    .withImportOption(new ImportOption.DoNotIncludeTests())
                    .importPackages("org.eolang.lints")
            );
    }

    /**
     * Snapshot of every lint name produced by {@link PkMono}.
     * @return Set of lint names
     */
    private static Set<String> lintNames() {
        return StreamSupport.stream(new PkMono().spliterator(), false)
            .map(Lint::name).collect(Collectors.toSet());
    }

    /**
     * Pull the lint name out of a motive resource filename.
     * @param res Classpath resource for a motive markdown file
     * @return Lint name (filename without {@code .md})
     */
    private static String shortName(final Resource res) {
        final String filename = res.getFilename();
        if (filename == null) {
            throw new IllegalStateException(
                String.format("Resource %s has no filename", res)
            );
        }
        return filename.replaceAll("\\.md$", "");
    }

    /**
     * Found decorated lint of specific lint.
     * @param decorate Lint
     * @return Decorated lint
     */
    @SuppressWarnings("PMD.AvoidAccessibilityAlteration")
    private static Lint decoratee(final Lint decorate) {
        Lint result = decorate;
        while (result.getClass().equals(LtDfSticky.class)) {
            final Field[] fields = decorate.getClass().getDeclaredFields();
            for (final Field field : fields) {
                field.setAccessible(true);
                final Object value;
                try {
                    value = field.get(decorate);
                } catch (final IllegalAccessException exception) {
                    throw new IllegalStateException("Failed to get decorated field", exception);
                }
                if (value instanceof Lint) {
                    result = (Lint) value;
                    break;
                }
            }
        }
        return result;
    }
}
