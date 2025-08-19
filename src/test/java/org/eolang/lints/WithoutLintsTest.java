/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
                new ListOf<>(new LtAtomIsNotUnique()),
                lid
            ).iterator().next().name(),
            Matchers.not(Matchers.equalTo(lid))
        );
    }

    @Test
    void doesNotExcludeNonExistingLints() {
        final List<Lint<Map<String, XML>>> original = new ListOf<>(new LtInconsistentArgs());
        MatcherAssert.assertThat(
            "Lint should not be excluded, since lint by provided name does not exist",
            new ListOf<>(new WithoutLints<>(original, "计算机科学")),
            Matchers.hasSize(original.size())
        );
    }

    @Test
    void excludesOnlySpecifiedLints() {
        MatcherAssert.assertThat(
            "The list of lints does not match with expected",
            new ListOf<>(
                new WithoutLints<>(
                    new ListOf<>(
                        new WithoutLintsTest.LtFake("foo"),
                        new WithoutLintsTest.LtFake("bar"),
                        new WithoutLintsTest.LtFake("jeff")
                    ),
                    "foo", "jeff"
                )
            ).stream().map(Lint::name).collect(Collectors.toList()),
            Matchers.allOf(
                Matchers.hasItem("bar"),
                Matchers.not(Matchers.hasItem("foo")),
                Matchers.not(Matchers.hasItem("jeff"))
            )
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

    /**
     * Fake lint.
     * @since 0.0.57
     */
    static final class LtFake implements Lint<XML> {

        /**
         * The lint name.
         */
        private final String lname;

        /**
         * Ctor.
         *
         * @param nme The lint name
         */
        LtFake(final String nme) {
            this.lname = nme;
        }

        @Override
        public String name() {
            return this.lname;
        }

        @Override
        public Collection<Defect> defects(final XML xmir) throws IOException {
            return Collections.emptyList();
        }

        @Override
        public String motive() {
            return "no motive";
        }
    }
}
