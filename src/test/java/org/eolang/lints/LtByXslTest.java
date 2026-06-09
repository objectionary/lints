/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.github.lombrozo.xnav.Xnav;
import com.jcabi.manifests.Manifests;
import com.jcabi.matchers.XhtmlMatchers;
import com.jcabi.xml.XMLDocument;
import fixtures.BytecodeClass;
import fixtures.EoProgram;
import fixtures.FixPack;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import matchers.DefectsMatcher;
import org.cactoos.io.InputOf;
import org.cactoos.io.ReaderOf;
import org.cactoos.io.ResourceOf;
import org.cactoos.map.MapOf;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;
import org.eolang.jucs.ClasspathSource;
import org.eolang.parser.StrictXmir;
import org.eolang.xax.XtSticky;
import org.eolang.xax.XtYaml;
import org.eolang.xax.XtoryMatcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.xembly.Directives;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;
import org.yaml.snakeyaml.Yaml;

/**
 * Test for {@link LtByXsl}.
 * @since 0.0.1
 * @checkstyle ClassFanOutComplexityCheck (500 lines)
 */
@SuppressWarnings("PMD.TooManyMethods")
final class LtByXslTest {

    @Test
    void producesExpectedXmirWithFix() throws Exception {
        final FixPack pack = new FixPack(
            new TextOf(
                new ResourceOf(
                    "org/eolang/lints/fixes/unsorted-metas/sorts-version-and-spdx.yaml"
                )
            ).asString()
        );
        MatcherAssert.assertThat(
            "LtByXsl fix must produce the same result as expected",
            pack.fixed(new LtByXsl("metas/unsorted-metas").fix()),
            Matchers.equalTo(pack.expected())
        );
    }

    @Test
    void lintsOneFile() {
        MatcherAssert.assertThat(
            "No defects found, while a few of them expected",
            new LtByXsl("critical/duplicate-names").defects(
                new EoProgram("org/eolang/lints/duplicate-names.eo").parse()
            ),
            Matchers.hasSize(Matchers.greaterThan(0))
        );
    }

    @SuppressWarnings("JTCOP.RuleNotContainsTestWord")
    @Execution(ExecutionMode.CONCURRENT)
    @ParameterizedTest
    @ClasspathSource(value = "org/eolang/lints/packs/single/", glob = "**.yaml")
    void testsAllLintsByEo(final String yaml, final String pack) {
        MatcherAssert.assertThat(
            String.format(
                "Pack '%s' doesn't tell the story as expected",
                pack
            ),
            new XtSticky(
                new XtYaml(
                    yaml,
                    eo -> new EoProgram(pack, new InputOf(eo)).parse()
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
            Matchers.equalTo(false)
        );
    }

    @Test
    @SuppressWarnings("StreamResourceLeak")
    void checksLocationsOfYamlPacks() throws IOException {
        MatcherAssert.assertThat(
            "All YAML packs must have corresponding XSL files",
            Files.walk(Paths.get("src/test/resources/org/eolang/lints/packs/single"))
                .filter(Files::isRegularFile)
                .allMatch(LtByXslTest::hasMatchingXsl),
            Matchers.equalTo(true)
        );
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    @SuppressWarnings("StreamResourceLeak")
    void catchesLostYamls() throws IOException {
        MatcherAssert.assertThat(
            "All YAML files must be in allowed locations (single, wpa packs, or fixes)",
            Files.walk(Paths.get("src/test/resources/org/eolang/lints"))
                .filter(Files::isRegularFile)
                .filter(LtByXslTest.yamls())
                .map(path -> path.getParent().getParent().toString()).allMatch(
                    path ->
                        path.endsWith("org/eolang/lints/packs/single")
                            || path.endsWith("org/eolang/lints/packs/wpa")
                            || path.contains("org/eolang/lints/fixes")
                ),
            Matchers.equalTo(true)
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
            Matchers.equalTo(true)
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
            Matchers.equalTo(true)
        );
    }

    @Test
    @SuppressWarnings("StreamResourceLeak")
    void checksIdsInXslStylesheets() throws IOException {
        MatcherAssert.assertThat(
            "All XSL stylesheets must have @id matching their filename",
            Files.walk(Paths.get("src/main/resources/org/eolang/lints"))
                .filter(Files::isRegularFile)
                .filter(file -> file.getFileName().toString().endsWith(".xsl")).allMatch(
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
            Matchers.equalTo(true)
        );
    }

    @Test
    @SuppressWarnings("StreamResourceLeak")
    void checksMotivesForPresence() throws IOException {
        MatcherAssert.assertThat(
            "All XSL lints must have corresponding motive markdown files",
            Files.walk(Paths.get("src/main/resources/org/eolang/lints"))
                .filter(Files::isRegularFile)
                .filter(f -> f.getFileName().toString().endsWith(".xsl")).allMatch(
                    path -> Files.exists(
                        Path.of(
                            path.toString()
                                .replace("lints", "motives")
                                .replace(".xsl", ".md")
                        )
                    )
                ),
            Matchers.equalTo(true)
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
    void returnsNonExperimentalWhenXslStaysQuiet() {
        MatcherAssert.assertThat(
            "Experimental flag should be set to false",
            new LtByXsl("comments/comment-without-dot").defects(
                new EoProgram("org/eolang/lints/foo-without-dot.eo").parse()
            ).stream().findFirst().get().experimental(),
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
            Matchers.equalTo(true)
        );
    }

    @Test
    @SuppressWarnings("StreamResourceLeak")
    void validatesPacksAgainstXsdSchema() throws IOException {
        MatcherAssert.assertThat(
            "All packs with 'document' field must validate against XSD schema",
            Files.walk(Paths.get("src/test/resources/org/eolang/lints/packs/single"))
                .filter(Files::isRegularFile)
                .filter(LtByXslTest.yamls()).map(
                    (Function<Path, Map<Path, Map<String, Object>>>)
                        p -> new MapOf<>(p, new Yaml().load(new ReaderOf(p.toFile())))
                ).filter(
                    pack -> {
                        final Map<String, Object> yaml = pack.values().stream().findFirst().get();
                        return yaml.containsKey("document") && !yaml.containsKey("ignore-schema");
                    }
                ).allMatch(LtByXslTest::schemaValid),
            Matchers.equalTo(true)
        );
    }

    @Test
    void doesNotDuplicateDefectsWhenMultipleDefectsOnTheSameLine() {
        final Collection<Defect> defects = new LtByXsl("misc/unused-void-attr").defects(
            new EoProgram("org/eolang/lints/unused-voids.eo").parse()
        );
        MatcherAssert.assertThat(
            "Found defects should not contain duplicates",
            new HashSet<>(defects).size() == defects.size(),
            Matchers.equalTo(true)
        );
    }

    @SuppressWarnings("StreamResourceLeak")
    @Tag("deep")
    @Timeout(300L)
    @Test
    void validatesEoPacksForErrors() throws IOException {
        final List<Path> failures = Files.walk(
            Paths.get("src/test/resources/org/eolang/lints/packs/single")
        ).filter(Files::isRegularFile)
            .filter(LtByXslTest.yamls()).map(
                (Function<Path, Map<Path, Map<String, Object>>>)
                    p -> new MapOf<>(p, new Yaml().load(new ReaderOf(p.toFile())))
            )
            .filter(pack -> pack.values().stream().findFirst().get().containsKey("input"))
            .filter(pack -> !LtByXslTest.eoErrorFree(pack))
            .map(pack -> pack.keySet().iterator().next())
            .collect(Collectors.toList());
        MatcherAssert.assertThat(
            String.format("These EO packs have parse errors: %s", failures),
            failures,
            Matchers.empty()
        );
    }

    private static boolean schemaValid(final Map<Path, Map<String, Object>> pack) {
        boolean valid;
        try {
            new StrictXmir(
                new XMLDocument(
                    new Xembler(
                        new Directives().xpath("/object").attr(
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

    private static boolean eoErrorFree(final Map<Path, Map<String, Object>> pack) {
        return new Xnav(
            new EoProgram(
                pack.keySet().iterator().next().toString(),
                new InputOf(
                    (String) pack.values().stream().findFirst().get().get("input")
                )
            ).parse().inner()
        ).path("/object[errors]").findAny().isEmpty();
    }

    /**
     * Filter YAML files.
     * @return Predicate
     */
    @SuppressWarnings("UnnecessaryLambda")
    private static Predicate<Path> yamls() {
        return path -> path.toString().endsWith(".yaml");
    }

    /**
     * Check if YAML pack path has a corresponding XSL file.
     * @param yaml YAML pack path
     * @return True if matching XSL exists
     */
    @SuppressWarnings("StreamResourceLeak")
    private static boolean hasMatchingXsl(final Path yaml) {
        try {
            return Files.walk(Paths.get("src/main/resources/org/eolang/lints"))
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".xsl"))
                .map(path -> path.getParent().getFileName().toString()).anyMatch(
                    group -> Files.exists(
                        Paths.get("src/main/resources/org/eolang/lints/").resolve(group).resolve(
                            String.format(
                                "%s.xsl",
                                yaml.getParent().getFileName().toString()
                            )
                        )
                    )
                );
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
