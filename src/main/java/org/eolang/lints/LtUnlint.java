/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.github.lombrozo.xnav.Xnav;
import com.google.common.base.Splitter;
import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.cactoos.list.ListOf;

/**
 * Lint that ignores linting if {@code +unlint} meta is present.
 *
 * @since 0.0.1
 */
final class LtUnlint implements Lint<XML> {

    /**
     * Line number to unlint.
     */
    private static final Pattern LINE_NUMBER = Pattern.compile(".*:\\d+$");

    /**
     * The original lint.
     */
    private final Lint<XML> origin;

    /**
     * Ctor.
     * @param lint The lint to decorate
     */
    LtUnlint(final Lint<XML> lint) {
        this.origin = lint;
    }

    @Override
    public String name() {
        return this.origin.name();
    }

    @Override
    public Collection<Defect> defects(final XML xmir) throws IOException {
        final Collection<Defect> defects = new ArrayList<>(0);
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
                if (unlint.matches(String.format("%s:\\d+-\\d+", lname))) {
                    problematic.removeIf(line -> LtUnlint.inRange(line, unlint, lname));
                } else if (LtUnlint.LINE_NUMBER.matcher(unlint).matches()) {
                    final List<String> split = new ListOf<>(unlint.split(":"));
                    final int lineno = Integer.parseInt(
                        split.get(1)
                    );
                    problematic.removeIf(line -> line == lineno);
                } else {
                    problematic.clear();
                }
            }
        );
        problematic.forEach(
            line -> found.forEach(
                defect -> {
                    if (line != 0 && defect.line() == line) {
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

    /**
     * Is the line in the unlint range?
     * @param line The line
     * @param unlint Unlint expression
     * @param lint Lint name
     * @return True - if in range, False - if out of range
     */
    private static boolean inRange(final int line, final String unlint, final String lint) {
        final List<String> range = Splitter.on('-').splitToList(
            unlint.replace(String.format("%s:", lint), "")
        );
        return line >= Integer.parseInt(range.get(0)) && line <= Integer.parseInt(range.get(1));
    }

}
