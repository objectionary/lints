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
 * Tests for {@link MonoLintNames}.
 *
 * @since 0.0.43
 */
final class MonoLintNamesTest {

    @Test
    @SuppressWarnings("JTCOP.RuleAssertionMessage")
    void staysPackagePrivate() {
        ArchRuleDefinition.classes()
            .that().haveSimpleName("MonoLintNames")
            .should().bePackagePrivate()
            .check(new ClassFileImporter()
                .withImportOption(new ImportOption.DoNotIncludeTests())
                .importPackages("org.eolang.lints")
            );
    }
}
