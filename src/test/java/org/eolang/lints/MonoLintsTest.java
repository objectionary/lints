/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import java.util.stream.Collectors;
import org.cactoos.list.ListOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link MonoLints}.
 *
 * @since 0.0.43
 */
final class MonoLintsTest {

    @Test
    void staysPackagePrivate() {
        ArchRuleDefinition.classes()
            .that().haveSimpleName("MonoLints")
            .should().bePackagePrivate()
            .check(new ClassFileImporter()
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
}
