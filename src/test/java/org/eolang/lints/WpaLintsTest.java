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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import matchers.WpaStoryMatcher;
import org.cactoos.iterable.Mapped;
import org.cactoos.list.ListOf;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.eolang.jucs.ClasspathSource;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * Tests for {@link WpaLints}.
 *
 * @since 0.0.43
 */
final class WpaLintsTest {

    /**
     * WPA lints mapped to their names.
     */
    private static final Map<String, Lint<Map<String, XML>>> WPA =
        new MapOf<String, Lint<Map<String, XML>>>(
            new Mapped<>(
                wpl -> new MapEntry<>(wpl.name(), wpl),
                new WpaLints()
            )
        );

    @Test
    @SuppressWarnings("JTCOP.RuleAssertionMessage")
    void staysPackagePrivate() {
        ArchRuleDefinition.classes()
            .that().haveSimpleName("WpaLints")
            .should().bePackagePrivate()
            .check(new ClassFileImporter()
                .withImportOption(new ImportOption.DoNotIncludeTests())
                .importPackages("org.eolang.lints")
            );
    }

    @SuppressWarnings("JTCOP.RuleNotContainsTestWord")
    @ParameterizedTest
    @ClasspathSource(value = "org/eolang/lints/packs/wpa/", glob = "**.yaml")
    void testsAllLintsByEo(final String yaml) throws IOException {
        MatcherAssert.assertThat(
            "Story failures are not empty, but they should.",
            new WpaStory(yaml, WpaLintsTest.WPA).execute(),
            new WpaStoryMatcher()
        );
    }

    @Test
    @SuppressWarnings("StreamResourceLeak")
    void checksLocationOfYamlPacks() throws IOException {
        final List<String> groups = new ListOf<>(new WpaLints()).stream()
            .map(Lint::name)
            .collect(Collectors.toList());
        Files.walk(Paths.get("src/test/resources/org/eolang/lints/packs/wpa"))
            .filter(Files::isRegularFile)
            .forEach(
                path -> {
                    final String lint = path.getParent().getFileName().toString();
                    MatcherAssert.assertThat(
                        String.format(
                            "Can't find lint for %s/%s, which must have name '%s'",
                            lint, path.getFileName(), lint
                        ),
                        groups.contains(lint),
                        new IsEqual<>(true)
                    );
                }
            );
    }

    @Test
    void checksMotivesForPresence() {
        new WpaLints().forEach(
            wpl -> {
                try {
                    MatcherAssert.assertThat(
                        String.format(
                            "Motive is missing for lint '%s'",
                            wpl.name()
                        ),
                        wpl.motive(),
                        Matchers.not(Matchers.emptyString())
                    );
                } catch (final IOException exception) {
                    throw new IllegalStateException(
                        String.format(
                            "Failed to read motive for '%s' lint", wpl.name()
                        ),
                        exception
                    );
                }
            }
        );
    }
}
