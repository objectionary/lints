/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.github.lombrozo.xnav.Xnav;
import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.cactoos.io.ResourceOf;
import org.cactoos.list.ListOf;
import org.cactoos.text.IoCheckedText;
import org.cactoos.text.TextOf;

/**
 * Lint for checking `+unlint` meta to suppress non-existing defects in WPA scope.
 * This lint was not included in {@link LtUnlintNonExistingDefect}, because we need
 * to aggregate the XMIR defects using supplied lints. In {@link LtUnlintNonExistingDefect}
 * we work with single program scope, while this class works with WPA scope.
 *
 * @see LtUnlintNonExistingDefect
 * @since 0.0.42
 */
final class LtUnlintNonExistingDefectWpa implements Lint<Map<String, XML>> {

    /**
     * Lints.
     */
    private final Iterable<Lint<Map<String, XML>>> lints;

    /**
     * Lint names for exclusion.
     */
    private final Collection<String> excluded;

    /**
     * Ctor.
     *
     * @param lnts Lints
     * @param exld Lint names to exclude
     */
    LtUnlintNonExistingDefectWpa(
        final Iterable<Lint<Map<String, XML>>> lnts, final Collection<String> exld
    ) {
        this.lints = lnts;
        this.excluded = exld;
    }

    @Override
    public String name() {
        return "unlint-non-existing-defect";
    }

    @Override
    public Collection<Defect> defects(final Map<String, XML> pkg) {
        final Collection<Defect> defects = new LinkedList<>();
        final Map<XML, List<String>> existing = this.existingDefects(pkg);
        pkg.values().forEach(
            xmir -> {
                final Xnav xml = new Xnav(xmir.inner());
                final List<String> present = existing.get(xmir);
                xml.path("/program/metas/meta[head='unlint']/tail")
                    .map(xnav -> xnav.text().get())
                    .collect(Collectors.toSet())
                    .stream()
                    .filter(unlint -> !present.contains(unlint) && !this.excluded.contains(unlint))
                    .forEach(
                        unlint -> xml
                            .path(
                                String.format(
                                    "program/metas/meta[head='unlint' and tail='%s']/@line", unlint
                                )
                            )
                            .map(xnav -> xnav.text().get())
                            .collect(Collectors.toList())
                            .forEach(
                                line -> defects.add(
                                    new Defect.Default(
                                        this.name(),
                                        Severity.WARNING,
                                        xml.element("program")
                                            .attribute("name")
                                            .text()
                                            .orElse("unknown"),
                                        Integer.parseInt(line),
                                        String.format(
                                            "Unlinting rule '%s' doesn't make sense, since there are no defects with it",
                                            unlint
                                        )
                                    )
                                )
                            )
                    );
            }
        );
        return defects;
    }

    @Override
    public String motive() throws IOException {
        return new IoCheckedText(
            new TextOf(
                new ResourceOf(
                    String.format(
                        "org/eolang/motives/misc/%s.md", this.name()
                    )
                )
            )
        ).asString();
    }

    /**
     * Find existing defects.
     *
     * @param pkg Package with programs to scan
     * @return Map of existing defects
     */
    private Map<XML, List<String>> existingDefects(final Map<String, XML> pkg) {
        final Map<XML, List<String>> aggregated = new HashMap<>(0);
        this.lints.forEach(
            wpl -> {
                try {
                    final Collection<Defect> defects = wpl.defects(pkg);
                    pkg.values().forEach(
                        program ->
                            aggregated.merge(
                                program,
                                new ListOf<>(
                                    defects.stream()
                                        .map(Defect::rule)
                                        .collect(Collectors.toList())
                                ),
                                (existing, incoming) -> {
                                    existing.addAll(incoming);
                                    return existing;
                                }
                            )
                    );
                } catch (final IOException exception) {
                    throw new IllegalStateException(
                        String.format(
                            "IO operation failed while linting package of programs with %s",
                            wpl.name()
                        ),
                        exception
                    );
                }
            }
        );
        return aggregated;
    }
}
