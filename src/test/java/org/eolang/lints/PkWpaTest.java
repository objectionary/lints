/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import com.yegor256.Together;
import org.cactoos.scalar.LengthOf;
import org.cactoos.set.SetOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link PkWpa}.
 *
 * @since 0.23
 */
final class PkWpaTest {

    @RepeatedTest(5)
    void createsLintsInParallel() {
        MatcherAssert.assertThat(
            "",
            new SetOf<>(
                new Together<>(
                    thread -> new LengthOf(new PkWpa()).value()
                )
            ).size(),
            Matchers.equalTo(1)
        );
    }

    @Test
    @SuppressWarnings("JTCOP.RuleAssertionMessage")
    void staysInsideThePackage() {
        ArchRuleDefinition.classes()
            .that().haveSimpleName("PkWpa")
            .should().bePackagePrivate()
            .check(new ClassFileImporter()
                .withImportOption(new ImportOption.DoNotIncludeTests())
                .importPackages("org.eolang.lints")
            );
    }
}
