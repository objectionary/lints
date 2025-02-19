/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2025 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.eolang.lints;

import com.jcabi.matchers.XhtmlMatchers;
import com.jcabi.xml.XMLDocument;
import fixtures.LargeXmir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;
import matchers.DefectsMatcher;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;
import org.eolang.jucs.ClasspathSource;
import org.eolang.parser.EoSyntax;
import org.eolang.xax.XtSticky;
import org.eolang.xax.XtYaml;
import org.eolang.xax.XtoryMatcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * Test for {@link LtByXsl}.
 *
 * @since 0.0.1
 */
@SuppressWarnings("PMD.TooManyMethods")
final class LtByXslTest {

    @Test
    void lintsOneFile() throws IOException {
        MatcherAssert.assertThat(
            "No defects found, while a few of them expected",
            new LtByXsl("critical/duplicate-names").defects(
                new EoSyntax(
                    "# first\n[] > foo\n# first\n[] > foo\n"
                ).parsed()
            ),
            Matchers.hasSize(Matchers.greaterThan(0))
        );
    }

    @ParameterizedTest
    @ClasspathSource(value = "org/eolang/lints/packs/", glob = "**.yaml")
    void testsAllLintsByEo(final String yaml) {
        MatcherAssert.assertThat(
            "Doesn't tell the story as it's expected",
            new XtSticky(
                new XtYaml(
                    yaml,
                    eo -> new EoSyntax("pack", eo).parsed()
                )
            ),
            new XtoryMatcher(new DefectsMatcher())
        );
    }

    @Test
    void returnsMotive() throws Exception {
        MatcherAssert.assertThat(
            "The motive was not found or empty",
            new LtByXsl("critical/duplicate-names").motive().isEmpty(),
            new IsEqual<>(false)
        );
    }

    @Test
    void testLocationsOfYamlPacks() throws IOException {
        final Set<String> groups = Files.walk(Paths.get("src/main/resources/org/eolang/lints"))
            .filter(Files::isRegularFile)
            .filter(path -> path.toString().endsWith(".xsl"))
            .map(path -> path.getParent().getFileName().toString())
            .collect(Collectors.toSet());
        Files.walk(Paths.get("src/test/resources/org/eolang/lints/packs"))
            .filter(Files::isRegularFile)
            .forEach(
                path -> {
                    final String lint = path.getParent().getFileName().toString();
                    final Path folder = Paths.get("src/main/resources/org/eolang/lints/");
                    boolean found = false;
                    for (final String group : groups) {
                        final Path xsl = folder.resolve(group).resolve(
                            String.format("%s.xsl", lint)
                        );
                        if (Files.exists(xsl)) {
                            found = true;
                            break;
                        }
                    }
                    MatcherAssert.assertThat(
                        String.format(
                            "Can't find XSL for %s/%s, which must be located at %s",
                            lint, path.getFileName(), folder
                        ),
                        found,
                        new IsEqual<>(true)
                    );
                }
            );
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    void catchesLostYamls() throws IOException {
        Files.walk(Paths.get("src/test/resources/org/eolang/lints"))
            .filter(Files::isRegularFile)
            .filter(path -> path.toString().endsWith(".yaml"))
            .map(path -> path.getParent().getParent().toString())
            .collect(Collectors.toSet())
            .forEach(
                path -> MatcherAssert.assertThat(
                    String.format(
                        "YAML file is at the wrong place: %s",
                        path
                    ),
                    path,
                    Matchers.endsWith("org/eolang/lints/packs")
                )
            );
    }

    @Test
    void catchesLostNonYamls() throws IOException {
        Files.walk(Paths.get("src/test/resources/org/eolang/lints/packs"))
            .filter(Files::isRegularFile)
            .forEach(
                path -> MatcherAssert.assertThat(
                    String.format(
                        "This is not a YAML file, but it's here: %s",
                        path
                    ),
                    path.toAbsolutePath().toString(),
                    Matchers.endsWith(".yaml")
                )
            );
    }

    @Test
    void checksFileNaming() throws IOException {
        Files.walk(Paths.get("src/test/resources/org/eolang/lints/packs"))
            .filter(Files::isRegularFile)
            .forEach(
                path -> MatcherAssert.assertThat(
                    String.format("Only YAML files are allowed here, while: %s", path),
                    path.toFile().toString().endsWith(".yaml"),
                    new IsEqual<>(true)
                )
            );
    }

    @Test
    void checksIdsInXslStylesheets() throws IOException {
        Files.walk(Paths.get("src/main/resources/org/eolang/lints"))
            .filter(Files::isRegularFile)
            .filter(file -> file.getFileName().toString().endsWith(".xsl"))
            .forEach(
                path -> MatcherAssert.assertThat(
                    String.format("@id is wrong in: %s", path),
                    XhtmlMatchers.xhtml(
                        new UncheckedText(new TextOf(path)).asString()
                    ),
                    XhtmlMatchers.hasXPath(
                        String.format(
                            "/xsl:stylesheet[@id='%s']",
                            path.getFileName().toString().replaceAll("\\.xsl$", "")
                        )
                    )
                )
            );
    }

    @Test
    void checksMotivesForPresence() throws IOException {
        Files.walk(Paths.get("src/main/resources/org/eolang/lints"))
            .filter(Files::isRegularFile)
            .filter(f -> f.getFileName().toString().endsWith(".xsl"))
            .forEach(
                path -> {
                    final Path motive = Path.of(
                        path.toString()
                            .replace("lints", "motives")
                            .replace(".xsl", ".md")
                    );
                    MatcherAssert.assertThat(
                        String.format(
                            "Motive file '%s' is missing for lint '%s'",
                            motive, path
                        ),
                        Files.exists(motive),
                        new IsEqual<>(true)
                    );
                }
            );
    }

    @Test
    @Timeout(30L)
    void checksEmptyObjectOnLargeXmirInReasonableTime() {
        Assertions.assertDoesNotThrow(
            () -> new LtByXsl("errors/empty-object").defects(
                new LargeXmir().value()
            ),
            "Huge XMIR must pass in reasonable time. See the timeout value."
        );
    }

    @Test
    void cutsLargeContext() throws Exception {
        new LtByXsl("critical/application-duality").defects(
            new XMLDocument(
                new ResourceOf(
                    "org/eolang/lints/xmir-with-application-duality.xml"
                ).stream()
            )
        ).forEach(
            defect ->
                MatcherAssert.assertThat(
                    "Defect context size is too big",
                    defect.context().length(),
                    Matchers.lessThanOrEqualTo(300)
                )
        );
    }
}
