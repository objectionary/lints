/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.yegor256.Mktmp;
import com.yegor256.MktmpResolver;
import com.yegor256.Together;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Collectors;
import matchers.DefectMatcher;
import org.cactoos.list.ListOf;
import org.cactoos.map.MapOf;
import org.cactoos.set.SetOf;
import org.eolang.parser.EoSyntax;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test for {@link Programs}.
 *
 * @since 0.1.0
 */
@ExtendWith(MktmpResolver.class)
final class ProgramsTest {

    @Test
    void checksSimple(@Mktmp final Path dir) throws IOException {
        MatcherAssert.assertThat(
            "the defect is found",
            new Programs(
                this.withProgram(
                    dir,
                    "a/b/c/foo.xmir",
                    "# first.\n[] > foo\n# second.\n[] > foo\n"
                )
            ).defects(),
            Matchers.allOf(
                Matchers.<Defect>iterableWithSize(Matchers.greaterThan(0)),
                Matchers.<Defect>everyItem(new DefectMatcher())
            )
        );
    }

    @Test
    void skipsAllWarnings(@Mktmp final Path dir) throws IOException {
        MatcherAssert.assertThat(
            "the defect is found",
            new Programs(
                this.withProgram(
                    dir,
                    "bar.xmir",
                    String.join(
                        "\n",
                        "+unlint unit-test-missing",
                        "",
                        "# Test.",
                        "[] > foo"
                    )
                )
            ).defects(),
            Matchers.emptyIterable()
        );
    }

    @Tag("deep")
    @RepeatedTest(5)
    void checksInParallel(@Mktmp final Path dir) throws IOException {
        final Path program = this.withProgram(
            dir,
            "foo.xmir",
            "# first.\n# second.\n[] > foo\n"
        );
        MatcherAssert.assertThat(
            "",
            new SetOf<>(
                new Together<>(
                    thread -> new Programs(program).defects().size()
                )
            ).size(),
            Matchers.equalTo(1)
        );
    }

    @Test
    void doesNotThrowIoException() {
        Assertions.assertDoesNotThrow(
            () -> new Programs(new ListOf<>()).defects(),
            "Exception was thrown, but it should not be"
        );
    }

    @Test
    void createsProgramsWithoutOneLint(@Mktmp final Path dir) throws IOException {
        final String disabled = "unit-test-missing";
        MatcherAssert.assertThat(
            "Defects for disabled lint are not empty, but should be",
            new Programs(
                this.withProgram(
                    dir,
                    "bar.xmir",
                    "# first.\n# second.\n[] > bar\n"
                )
            ).without(disabled).defects().stream()
                .filter(defect -> defect.rule().equals(disabled))
                .collect(Collectors.toList()),
            Matchers.emptyIterable()
        );
    }

    @Test
    void createsProgramsWithoutMultipleLints(@Mktmp final Path dir) throws IOException {
        MatcherAssert.assertThat(
            "Defects for disabled lint are not empty, but should be",
            new Programs(
                this.withProgram(
                    dir,
                    "bar.xmir",
                    "# first.\n# second.\n[] > bar\n"
                ),
                this.withProgram(
                    dir,
                    "foo-test.xmir",
                    "# first.\n# second.\n[] > x\n"
                )
            ).without("unit-test-missing", "unit-test-without-live-file").defects(),
            Matchers.emptyIterable()
        );
    }

    @ParameterizedTest
    @ValueSource(
        strings = {"unit-test-missing", "unit-test-missing:0"}
    )
    void catchesNonExistingDefectAfterLintWasRemoved(final String lid) throws IOException {
        final Collection<Defect> found = new Programs(
            new MapOf<>(
                "f",
                new EoSyntax(
                    "f",
                    String.join(
                        "\n",
                        String.format("+unlint %s", lid),
                        "",
                        "# F.",
                        "[] > f"
                    )
                ).parsed()
            )
        ).without("unit-test-missing").defects();
        MatcherAssert.assertThat(
            "Defects were not found, though code is broken",
            found,
            Matchers.hasSize(Matchers.greaterThan(0))
        );
        MatcherAssert.assertThat(
            "Found defect does not match with expected",
            new ListOf<>(found).get(0).text(),
            Matchers.containsString(
                String.format(
                    "Unlinting rule '%s' doesn't make sense",
                    lid
                )
            )
        );
    }

    private Path withProgram(final Path dir, final String name,
        final String text) throws IOException {
        final Path path = dir.resolve(name);
        path.toFile().getParentFile().mkdirs();
        Files.write(
            path,
            new EoSyntax(
                text
            ).parsed().toString().getBytes(StandardCharsets.UTF_8)
        );
        return dir;
    }

}
