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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Lint that ignores linting if {@code +unlint} meta is present.
 *
 * @since 0.0.1
 */
final class LtUnlint implements Lint {

    /**
     * The original lint.
     */
    private final Lint origin;

    /**
     * Ctor.
     *
     * @param lint The lint to decorate
     */
    LtUnlint(final Lint lint) {
        this.origin = lint;
    }

    @Override
    public String name() {
        return this.origin.name();
    }

    @Override
    public Collection<Defect> defects(final XML xmir) throws IOException {
        // @checkstyle ConditionalRegexpMultilineCheck (1 line)
        final Collection<Defect> defects = new ArrayList<>();
        final String lname = this.origin.name();
        final Collection<Defect> found = this.origin.defects(xmir);
        final List<Integer> problematic = found.stream()
            .filter(defect -> defect.rule().equals(lname))
            .map(Defect::line)
            .distinct()
            .collect(Collectors.toList());
        final List<String> granular = new Xnav(xmir.inner()).path(
            String.format(
                "/object/metas/meta[head='unlint' and (tail='%s' or starts-with(tail, '%s:'))]/tail",
                lname, lname
            )
        ).map(xnav -> xnav.text().get()).collect(Collectors.toList());
        final boolean global = !granular.isEmpty();
        final AtomicBoolean added = new AtomicBoolean(false);
        granular.forEach(
            unlint -> {
                final String quoted = Pattern.quote(lname);
                if (unlint.matches(String.format("%s:\\d+-\\d+", quoted))) {
                    problematic.removeIf(new UnlintInRange(unlint));
                } else if (unlint.matches(String.format("%s:\\d+", quoted))) {
                    final String selected = unlint.substring(lname.length() + 1);
                    if (LtUnlint.fitsInt(selected)) {
                        problematic.removeIf(line -> line == Integer.parseInt(selected));
                    }
                } else if (unlint.equals(lname)) {
                    problematic.clear();
                }
            }
        );
        problematic.forEach(
            line -> found.forEach(
                defect -> {
                    if (defect.line() == line) {
                        defects.add(defect);
                        added.set(true);
                    }
                }
            )
        );
        if (!added.get() && !global) {
            defects.addAll(found);
        }
        return defects;
    }

    @Override
    public String motive() throws IOException {
        return this.origin.motive();
    }

    @Override
    public Fix fix() {
        return this.origin.fix();
    }

    private static boolean fitsInt(final String value) {
        boolean result;
        try {
            Integer.parseInt(value);
            result = true;
        } catch (final NumberFormatException exception) {
            result = false;
        }
        return result;
    }
}
