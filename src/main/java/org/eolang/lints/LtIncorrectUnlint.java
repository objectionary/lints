/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.github.lombrozo.xnav.Xnav;
import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import org.cactoos.io.ResourceOf;
import org.cactoos.set.SetOf;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;
import org.eolang.parser.OnDefault;

/**
 * Lint that all unlint metas point to existing lint.
 *
 * @since 0.0.38
 */
final class LtIncorrectUnlint implements Lint<XML> {

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
        final Collection<Defect> defects = new ArrayList<>(0);
        final Xnav xml = new Xnav(xmir.inner());
        xml.path("/object/metas/meta[head='unlint']")
            .filter(
                u -> !this.names.contains(
                    u.element("tail").text().orElse("unknown").split(":", -1)[0]
                )
            )
            .forEach(
                u -> defects.add(
                    new Defect.Default(
                        this.name(),
                        Severity.ERROR,
                        new OnDefault(xmir).get(),
                        Integer.parseInt(u.attribute("line").text().orElse("0")),
                        String.format(
                            "Suppressing \"%s\" does not make sense, because there is no lint with that name",
                            u.element("tail").text().orElse("unknown")
                        )
                    )
                )
            );
        return defects;
    }

    @Override
    public String motive() throws IOException {
        return new UncheckedText(
            new TextOf(
                new ResourceOf(
                    "org/eolang/motives/errors/lt-incorrect-unlint.md"
                )
            )
        ).asString();
    }
}
