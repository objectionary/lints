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
import java.util.function.Function;
import java.util.stream.Collectors;
import org.cactoos.io.ResourceOf;
import org.cactoos.list.ListOf;
import org.cactoos.text.IoCheckedText;
import org.cactoos.text.TextOf;
import org.eolang.parser.ObjectName;

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
     */
    LtUnlintNonExistingDefectWpa(final Iterable<Lint<Map<String, XML>>> lnts) {
        this(lnts, new ListOf<>());
    }

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
        final Map<XML, Map<String, List<Integer>>> existing = this.existingDefects(pkg);
        pkg.values().forEach(
            xmir -> {
                final Xnav xml = new Xnav(xmir.inner());
                final Function<String, Boolean> missing = new DefectMissing(
                    existing.get(xmir), this.excluded
                );
                xml.path("/object/metas/meta[head='unlint']/tail")
                    .map(xnav -> xnav.text().get())
                    .collect(Collectors.toSet())
                    .stream()
                    .filter(missing::apply)
                    .forEach(
                        unlint -> xml
                            .path(
                                String.format(
                                    "object/metas/meta[head='unlint' and tail='%s']/@line", unlint
                                )
                            )
                            .map(xnav -> xnav.text().get())
                            .collect(Collectors.toList())
                            .forEach(
                                line -> defects.add(
                                    new Defect.Default(
                                        this.name(),
                                        Severity.WARNING,
                                        new ObjectName(xmir).get(),
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
    private Map<XML, Map<String, List<Integer>>> existingDefects(final Map<String, XML> pkg) {
        final Map<XML, Map<String, List<Integer>>> aggregated = new HashMap<>(0);
        this.lints.forEach(
            wpl -> {
                try {
                    final Collection<Defect> defects = wpl.defects(pkg);
                    defects.forEach(
                        defect -> pkg.values().forEach(
                            program ->
                                aggregated
                                    .computeIfAbsent(program, k -> new HashMap<>())
                                    .computeIfAbsent(defect.rule(), k -> new ListOf<>())
                                    .add(defect.line())
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
