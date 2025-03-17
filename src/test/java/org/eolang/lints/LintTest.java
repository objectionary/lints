/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Lint}.
 *
 * @since 0.23
 */
final class LintTest {

    @Test
    @SuppressWarnings("JTCOP.RuleAssertionMessage")
    void ensuresEveryLintHasProperPrefix() {
        ArchRuleDefinition.classes()
            .that().haveSimpleNameStartingWith("Lt")
            .should().implement(Lint.class)
            .check(new ClassFileImporter()
                .withImportOption(new ImportOption.DoNotIncludeTests())
                .importPackages("org.eolang.lints")
            );
    }

    @Test
    @SuppressWarnings("JTCOP.RuleAssertionMessage")
    void ensuresLintsStayInTheirPackages() {
        ArchRuleDefinition.classes()
            .that().implement(Lint.class)
            .should().bePackagePrivate()
            .check(new ClassFileImporter()
                .withImportOption(new ImportOption.DoNotIncludeTests())
                .importPackages("org.eolang.lints")
            );
    }
}
