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
 *
 * @since 0.0.38
 */
final class LtIncorrectUnlint implements Lint {

    /**
     * All possible names.
     */
    private final Collection<String> names;

    /**
     * Ctor.
     *
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
            u -> {
                final String tail = LtIncorrectUnlint.tail(u);
                final String name = LtIncorrectUnlint.nameOf(tail);
                return !this.names.contains(name) || LtIncorrectUnlint.invalid(tail, name);
            }
        ).map(u -> this.defect(u)).collect(Collectors.toList());
    }

    @Override
    public String motive() throws IOException {
        return new MotiveFrom("errors", "incorrect-unlint").asString();
    }

    @Override
    public Fix fix() {
        return new FxEmpty();
    }

    private Defect defect(final Xnav meta) {
        final String tail = LtIncorrectUnlint.tail(meta);
        final String name = LtIncorrectUnlint.nameOf(tail);
        final String msg;
        if (this.names.contains(name)) {
            msg = String.format(
                "Suppressing \"%s\" has an invalid format, use \"%s\", \"%s:N\" or \"%s:N-M\"",
                tail, name, name, name
            );
        } else {
            msg = String.format(
                "Suppressing \"%s\" does not make sense, because there is no lint with that name",
                tail
            );
        }
        return new Defect.Default(
            this.name(),
            Severity.ERROR,
            new LineOf(meta).value(),
            msg
        );
    }

    private static String tail(final Xnav meta) {
        return meta.element("tail").text().orElse("unknown");
    }

    private static String nameOf(final String tail) {
        return tail.split(":", -1)[0];
    }

    private static boolean invalid(final String tail, final String name) {
        return !tail.equals(name)
            && !tail.matches(String.format("%s:\\d+", name))
            && !tail.matches(String.format("%s:\\d+-\\d+", name));
    }
}
