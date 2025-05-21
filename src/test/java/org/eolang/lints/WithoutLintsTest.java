/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.cactoos.list.ListOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests for {@link WithoutLints}.
 * @since 0.0.46
 */
final class WithoutLintsTest {

    @ParameterizedTest
    @ValueSource(
        strings = {"unit-test-missing", "unit-test-without-live-file"}
    )
    void excludesLints(final String lid) {
        MatcherAssert.assertThat(
            String.format(
                "Lint with name '%s' was not excluded, but it should",
                lid
            ),
            new WithoutLints<>(
                new ListOf<>(
                    new LtUnitTestMissing(),
                    new LtUnitTestWithoutLiveFile()
                ),
                lid
            ).iterator().next().name(),
            Matchers.not(Matchers.equalTo(lid))
        );
    }

    @Test
    @SuppressWarnings("JTCOP.RuleAssertionMessage")
    void staysPackagePrivate() {
        ArchRuleDefinition.classes()
            .that().haveSimpleName("WithoutLints")
            .should().bePackagePrivate()
            .check(new ClassFileImporter()
                .withImportOption(new ImportOption.DoNotIncludeTests())
                .importPackages("org.eolang.lints")
            );
    }
}
