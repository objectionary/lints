/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.github.lombrozo.xnav.Xnav;
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
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
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
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.params.ParameterizedTest;
import org.xembly.Directives;
import org.xembly.ImpossibleModificationException;
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
        final Path folder = Paths.get("src/main/resources/org/eolang/lints/");
        MatcherAssert.assertThat(
            "All YAML packs must have corresponding XSL files",
            Files.walk(Paths.get("src/test/resources/org/eolang/lints/packs/single"))
                .filter(Files::isRegularFile)
                .allMatch(
                    path -> {
                        final String lint = path.getParent().getFileName().toString();
                        return groups.stream().anyMatch(
                            group -> Files.exists(
                                folder.resolve(group).resolve(String.format("%s.xsl", lint))
                            )
                        );
                    }
                ),
            new IsEqual<>(true)
        );
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    @SuppressWarnings("StreamResourceLeak")
    void catchesLostYamls() throws IOException {
        MatcherAssert.assertThat(
            "All YAML files must be in allowed locations (single or wpa packs)",
            Files.walk(Paths.get("src/test/resources/org/eolang/lints"))
                .filter(Files::isRegularFile)
                .filter(LtByXslTest.yamls())
                .map(path -> path.getParent().getParent().toString())
                .allMatch(
                    path ->
                        path.endsWith("org/eolang/lints/packs/single")
                            || path.endsWith("org/eolang/lints/packs/wpa")
                ),
            new IsEqual<>(true)
        );
    }

    @Test
    @SuppressWarnings("StreamResourceLeak")
    void catchesLostNonYamls() throws IOException {
        MatcherAssert.assertThat(
            "All files in packs directory must have .yaml extension",
            Files.walk(Paths.get("src/test/resources/org/eolang/lints/packs"))
                .filter(Files::isRegularFile)
                .allMatch(path -> path.toAbsolutePath().toString().endsWith(".yaml")),
            new IsEqual<>(true)
        );
    }

    @Test
    @SuppressWarnings("StreamResourceLeak")
    void checksFileNaming() throws IOException {
        MatcherAssert.assertThat(
            "All files in packs directory must be YAML",
            Files.walk(Paths.get("src/test/resources/org/eolang/lints/packs"))
                .filter(Files::isRegularFile)
                .allMatch(path -> path.toFile().toString().endsWith(".yaml")),
            new IsEqual<>(true)
        );
    }

    @Test
    @SuppressWarnings("StreamResourceLeak")
    void checksIdsInXslStylesheets() throws IOException {
        MatcherAssert.assertThat(
            "All XSL stylesheets must have @id matching their filename",
            Files.walk(Paths.get("src/main/resources/org/eolang/lints"))
                .filter(Files::isRegularFile)
                .filter(file -> file.getFileName().toString().endsWith(".xsl"))
                .allMatch(
                    path -> XhtmlMatchers.hasXPath(
                        String.format(
                            "/xsl:stylesheet[@id='%s']",
                            path.getFileName().toString().replaceAll("\\.xsl$", "")
                        )
                    ).matches(
                        XhtmlMatchers.xhtml(
                            new UncheckedText(new TextOf(path)).asString()
                        )
                    )
                ),
            new IsEqual<>(true)
        );
    }

    @Test
    @SuppressWarnings("StreamResourceLeak")
    void checksMotivesForPresence() throws IOException {
        MatcherAssert.assertThat(
            "All XSL lints must have corresponding motive markdown files",
            Files.walk(Paths.get("src/main/resources/org/eolang/lints"))
                .filter(Files::isRegularFile)
                .filter(f -> f.getFileName().toString().endsWith(".xsl"))
                .allMatch(
                    path -> Files.exists(
                        Path.of(
                            path.toString()
                                .replace("lints", "motives")
                                .replace(".xsl", ".md")
                        )
                    )
                ),
            new IsEqual<>(true)
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
        MatcherAssert.assertThat(
            "All defect contexts must be within 300 character limit",
            new LtByXsl("errors/application-without-as-attributes").defects(
                new XMLDocument(
                    new ResourceOf(
                        "org/eolang/lints/xmir-with-application-duality.xml"
                    ).stream()
                )
            ).stream().allMatch(defect -> defect.context().length() <= 300),
            new IsEqual<>(true)
        );
    }

    @Test
    @SuppressWarnings("StreamResourceLeak")
    void validatesPacksAgainstXsdSchema() throws IOException {
        MatcherAssert.assertThat(
            "All packs with 'document' field must validate against XSD schema",
            Files.walk(Paths.get("src/test/resources/org/eolang/lints/packs/single"))
                .filter(Files::isRegularFile)
                .filter(LtByXslTest.yamls())
                .map(
                    (Function<Path, Map<Path, Map<String, Object>>>)
                        p -> new MapOf<>(p, new Yaml().load(new ReaderOf(p.toFile())))
                )
                .filter(
                    pack -> {
                        final Map<String, Object> yaml = pack.values().stream().findFirst().get();
                        return yaml.containsKey("document") && !yaml.containsKey("ignore-schema");
                    }
                )
                .allMatch(LtByXslTest::schemaValid),
            new IsEqual<>(true)
        );
    }

    private static boolean schemaValid(final Map<Path, Map<String, Object>> pack) {
        boolean valid;
        try {
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
                            (String) pack.values().stream().findFirst().get().get("document")
                        ).inner()
                    )
                )
            ).inner();
            valid = true;
        } catch (final ImpossibleModificationException ex) {
            valid = false;
        }
        return valid;
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

    @SuppressWarnings("StreamResourceLeak")
    @Tag("deep")
    @Timeout(unit = TimeUnit.MINUTES, value = 5L)
    @Test
    void validatesEoPacksForErrors() throws IOException {
        MatcherAssert.assertThat(
            "All EO snippets in packs must parse without errors",
            Files.walk(Paths.get("src/test/resources/org/eolang/lints/packs/single"))
                .filter(Files::isRegularFile)
                .filter(LtByXslTest.yamls())
                .map(
                    (Function<Path, Map<Path, Map<String, Object>>>)
                        p -> new MapOf<>(p, new Yaml().load(new ReaderOf(p.toFile())))
                )
                .filter(pack -> pack.values().stream().findFirst().get().containsKey("input"))
                .allMatch(LtByXslTest::eoErrorFree),
            new IsEqual<>(true)
        );
    }

    private static boolean eoErrorFree(final Map<Path, Map<String, Object>> pack) {
        try {
            return new Xnav(
                new EoSyntax(
                    (String) pack.values().stream().findFirst().get().get("input")
                ).parsed().inner()
            ).path("/object[errors]").findAny().isEmpty();
        } catch (final IOException exception) {
            throw new IllegalStateException(
                String.format(
                    "Failed to parse EO snippet from '%s' pack",
                    pack.keySet().iterator().next()
                ),
                exception
            );
        }
    }

    /**
     * Filter YAML files.
     * @return Predicate
     */
    @SuppressWarnings("UnnecessaryLambda")
    private static Predicate<Path> yamls() {
        return path -> path.toString().endsWith(".yaml");
    }
}
