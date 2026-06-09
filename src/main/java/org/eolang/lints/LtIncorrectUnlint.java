/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.github.lombrozo.xnav.Xnav;
import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;
import org.cactoos.set.SetOf;

/**
 * Lint that all unlint metas point to existing lint.
 * @since 0.0.38
 */
final class LtIncorrectUnlint implements Lint {

    /**
     * All possible names.
     */
    private final Collection<String> names;

    /**
     * Ctor.
     * @param lints All possible lint names
     */
    LtIncorrectUnlint(final Iterable<String> lints) {
        this.names = new SetOf<>(lints);
    }

    @Override
    public String name() {
        return "incorrect-unlint";
    }

    @Override
    public Collection<Defect> defects(final XML xmir) throws IOException {
        return new Xnav(xmir.inner()).path("/object/metas/meta[head='unlint']").filter(
            u -> !this.names.contains(
                u.element("tail").text().orElse("unknown").split(":", -1)[0]
            )
        ).map(
            u -> new Defect.Default(
                this.name(),
                Severity.ERROR,
                new LineOf(u).value(),
                String.format(
                    "Suppressing \"%s\" does not make sense, because there is no lint with that name",
                    u.element("tail").text().orElse("unknown")
                )
            )
        ).collect(Collectors.toList());
    }

    @Override
    public String motive() throws IOException {
        return new MotiveFrom("errors", "incorrect-unlint").asString();
    }

    @Override
    public Fix fix() {
        return new FxEmpty();
    }
}
