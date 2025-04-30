/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.log.Logger;
import com.jcabi.manifests.Manifests;
import com.jcabi.matchers.XhtmlMatchers;
import com.jcabi.xml.XMLDocument;
import fixtures.BytecodeClass;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import matchers.DefectsMatcher;
import org.cactoos.io.ReaderOf;
import org.cactoos.io.ResourceOf;
import org.cactoos.list.ListOf;
import org.cactoos.map.MapOf;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;
import org.eolang.jucs.ClasspathSource;
import org.eolang.parser.EoSyntax;
import org.eolang.parser.StrictXmir;
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
import org.xembly.Directives;
import org.xembly.Xembler;
import org.yaml.snakeyaml.Yaml;

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
                    String.join(
                        "\n",
                        "# first.",
                        "[] > foo",
                        "  12 > x",
                        "  52 > x"
                    )
                ).parsed()
            ),
            Matchers.hasSize(Matchers.greaterThan(0))
        );
    }

    @SuppressWarnings("JTCOP.RuleNotContainsTestWord")
    @ParameterizedTest
    @ClasspathSource(value = "org/eolang/lints/packs/single/", glob = "**.yaml")
    void testsAllLintsByEo(final String yaml) {
        MatcherAssert.assertThat(
            "Doesn't tell the story as it's expected",
            new XtSticky(
                new XtYaml(
                    yaml,
                    eo -> new EoSyntax(eo).parsed()
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
    @SuppressWarnings("StreamResourceLeak")
    void checksLocationsOfYamlPacks() throws IOException {
        final Set<String> groups = Files.walk(Paths.get("src/main/resources/org/eolang/lints"))
            .filter(Files::isRegularFile)
            .filter(path -> path.toString().endsWith(".xsl"))
            .map(path -> path.getParent().getFileName().toString())
            .collect(Collectors.toSet());
        Files.walk(Paths.get("src/test/resources/org/eolang/lints/packs/single"))
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
    @SuppressWarnings("StreamResourceLeak")
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
                    Matchers.anyOf(
                        Matchers.endsWith("org/eolang/lints/packs/single"),
                        Matchers.endsWith("org/eolang/lints/packs/wpa")
                    )
                )
            );
    }

    @Test
    @SuppressWarnings("StreamResourceLeak")
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
    @SuppressWarnings("StreamResourceLeak")
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
    @SuppressWarnings("StreamResourceLeak")
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
    @SuppressWarnings("StreamResourceLeak")
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
    @Timeout(60L)
    void checksEmptyObjectOnLargeXmirInReasonableTime() {
        Assertions.assertDoesNotThrow(
            () -> new LtByXsl("errors/empty-object").defects(
                new BytecodeClass("com/sun/jna/Pointer.class").value()
            ),
            "Huge XMIR must pass in reasonable time. See the timeout value."
        );
    }

    @Test
    void returnsNonExperimentalWhenXslStaysQuiet() throws IOException {
        MatcherAssert.assertThat(
            "Experimental flag should be set to false",
            new ListOf<>(
                new LtByXsl("comments/comment-without-dot").defects(
                    new EoSyntax(
                        String.join(
                            "\n",
                            "# Foo",
                            "[] > foo"
                        )
                    ).parsed()
                )
            ).get(0).experimental(),
            Matchers.equalTo(false)
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

    @Test
    @SuppressWarnings("StreamResourceLeak")
    void validatesPacksAgainstXsdSchema() throws IOException {
        Files.walk(Paths.get("src/test/resources/org/eolang/lints/packs/single"))
            .filter(Files::isRegularFile)
            .filter(p -> p.toString().endsWith(".yaml"))
            .map(
                (Function<Path, Map<Path, Map<String, Object>>>)
                    p ->
                        new MapOf<>(p, new Yaml().load(new ReaderOf(p.toFile())))
            )
            .filter(
                pack -> {
                    final Map<String, Object> yaml = pack.values().stream().findFirst().get();
                    return yaml.containsKey("document") && !yaml.containsKey("ignore-schema");
                }
            )
            .forEach(
                pack ->
                    Assertions.assertDoesNotThrow(
                        () ->
                            new StrictXmir(
                                new XMLDocument(
                                    new Xembler(
                                        new Directives()
                                            .xpath("/object")
                                            .attr(
                                                "noNamespaceSchemaLocation xsi http://www.w3.org/2001/XMLSchema-instance",
                                                String.format(
                                                    "https://www.eolang.org/xsd/XMIR-%s.xsd",
                                                    Manifests.read("EO-Version")
                                                )
                                            )
                                    ).apply(
                                        new XMLDocument(
                                            (String) pack.values().stream().findFirst().get().get(
                                                "document"
                                            )
                                        ).inner()
                                    )
                                )
                            ).inner(),
                        String.format(
                            "Validation of XMIR in '%s' pack failed, but it should pass without errors",
                            pack.keySet().iterator().next()
                        )
                    )
            );
    }

    @Test
    void doesNotDuplicateDefectsWhenMultipleDefectsOnTheSameLine() throws Exception {
        final Collection<Defect> defects = new LtByXsl("misc/unused-void-attr").defects(
            new EoSyntax(
                String.join(
                    "\n",
                    "# Foo with unused voids on the same line.",
                    "[x y z] > foo"
                )
            ).parsed()
        );
        MatcherAssert.assertThat(
            Logger.format(
                "Found defects (%[list]s) should not contain duplicates",
                defects
            ),
            new HashSet<>(defects).size() == defects.size(),
            Matchers.equalTo(true)
        );
    }
}
