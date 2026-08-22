/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.github.lombrozo.xnav.Xnav;
import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.cactoos.list.ListOf;

/**
 * Lint for checking `+unlint` meta to suppress non-existing defects in single XMIR scope.
 * @since 0.0.40
 */
final class LtUnlintNonExistingDefect implements Lint {

    /**
     * Lints.
     */
    private final Iterable<Lint> lints;

    /**
     * Lints to exclude.
     */
    private final Collection<String> excluded;

    /**
     * Ctor.
     * @param lnts Lints
     */
    LtUnlintNonExistingDefect(final Iterable<Lint> lnts) {
        this(lnts, new ListOf<>());
    }

    /**
     * Ctor.
     * @param lnts Lints
     * @param exld Lint names to exclude
     */
    LtUnlintNonExistingDefect(final Iterable<Lint> lnts, final Collection<String> exld) {
        this.lints = lnts;
        this.excluded = exld;
    }

    @Override
    public String name() {
        return "unlint-non-existing-defect";
    }

    @Override
    public Collection<Defect> defects(final XML xmir) throws IOException {
        final List<String> unlints = new Xnav(xmir.inner())
            .path("/object/metas/meta[head='unlint']/tail")
            .map(xnav -> xnav.text().get())
            .distinct()
            .collect(Collectors.toList());
        final Collection<Defect> messages;
        if (unlints.isEmpty()) {
            messages = new ArrayList<>(0);
        } else {
            messages = unlints.stream().filter(
                new DefectMissing(this.existing(unlints, xmir), this.excluded)::apply
            ).flatMap(
                unlint -> new Xnav(xmir.inner()).path(
                    String.format(
                        "object/metas/meta[head='unlint' and tail=%s]/@line",
                        LtUnlintNonExistingDefect.quoted(unlint)
                    )
                ).map(
                    xnav -> new Defect.Default(
                        this.name(),
                        Severity.WARNING,
                        Integer.parseInt(xnav.text().get()),
                        String.format(
                            "Unlinting rule '%s' doesn't make sense, since there are no defects with it",
                            unlint
                        )
                    )
                )
            ).collect(Collectors.toList());
        }
        return messages;
    }

    @Override
    public String motive() throws IOException {
        return new MotiveFrom("misc", this.name()).asString();
    }

    @Override
    public Fix fix() {
        return new FxEmpty();
    }

    private static String quoted(final String value) {
        final String result;
        if (value.contains("\"")) {
            result = String.format("'%s'", value);
        } else {
            result = String.format("\"%s\"", value);
        }
        return result;
    }

    private Map<String, List<Integer>> existing(final List<String> unlints, final XML xmir) {
        final Set<String> names = unlints.stream()
            .map(unlint -> unlint.split(":", -1)[0])
            .collect(Collectors.toSet());
        return StreamSupport.stream(this.lints.spliterator(), false)
            .filter(lint -> names.contains(lint.name())).flatMap(
                lint -> {
                    try {
                        return lint.defects(xmir).stream();
                    } catch (final IOException exception) {
                        throw new IllegalStateException(
                            String.format("Failed to apply lint %s to XMIR", lint.name()),
                            exception
                        );
                    }
                }
            ).collect(
                Collectors.groupingBy(
                    Defect::rule,
                    Collectors.mapping(Defect::line, Collectors.toList())
                )
            );
    }
}
