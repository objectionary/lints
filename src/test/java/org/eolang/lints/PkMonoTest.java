/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import com.yegor256.Together;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.stream.Collectors;
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
 *
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
            new Program(
                new EoSyntax(
                    "+unlint ascii-only\n # привет\n# как дела?\n[] > foo\n"
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
        new ListOf<>(new PkMono()).stream()
            .filter(lint -> !lint.getClass().equals(LtIncorrectUnlint.class))
            .collect(Collectors.toList()).forEach(
                lint ->
                    MatcherAssert.assertThat(
                        String.format(
                            "Lint '%s' can not be unlinted, since its not wrapped by LtUnlint",
                            lint.name()
                        ),
                        PkMonoTest.decoratee(lint).getClass().equals(LtUnlint.class),
                        new IsEqual<>(true)
                    )
            );
    }

    @Test
    @SuppressWarnings("JTCOP.RuleAssertionMessage")
    void staysInsideThePackage() {
        ArchRuleDefinition.classes()
            .that().haveSimpleName("PkMono")
            .should().bePackagePrivate()
            .check(new ClassFileImporter()
                .withImportOption(new ImportOption.DoNotIncludeTests())
                .importPackages("org.eolang.lints")
            );
    }

    /**
     * Found decorated lint of specific lint.
     * @param decorate Lint
     * @return Decorated lint
     * @todo #394:25min Replace `LtAlways` in comparison with lint caching decorator.
     *  Currently, its not available, once <a href="https://github.com/objectionary/lints/issues/372">this</a>
     *  ticket will be solved, we can replace the class.
     */
    @SuppressWarnings("PMD.AvoidAccessibilityAlteration")
    private static Lint<?> decoratee(final Lint<XML> decorate) {
        Lint<?> result = decorate;
        while (decorate.getClass().equals(LtAlways.class)) {
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
                    result = (Lint<?>) value;
                    break;
                }
            }
        }
        return result;
    }

}
