/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2025 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.eolang.lints;

import com.github.lombrozo.xnav.Xnav;
import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;
import org.cactoos.io.ResourceOf;
import org.cactoos.list.ListOf;
import org.cactoos.text.IoCheckedText;
import org.cactoos.text.TextOf;

/**
 * Lints.
 * @since 0.0.0
 */
final class LtUnlintNonExistingDefect implements Lint<XML> {

    /**
     * Lints.
     */
    private final Iterable<Lint<XML>> lints;

    /**
     * Ctor.
     *
     * @param lnts Lints
     */
    LtUnlintNonExistingDefect(final Iterable<Lint<XML>> lnts) {
        this.lints = lnts;
    }

    @Override
    public String name() {
        return "unlint-non-existing-defect";
    }

    @Override
    public Collection<Defect> defects(final XML xmir) throws IOException {
        final Collection<Defect> defects = new LinkedList<>();
        final Collection<String> present = this.existingDefects(xmir);
        final Xnav xml = new Xnav(xmir.inner());
        final Set<String> unlints = xml.path("/program/metas/meta[head='unlint']/tail")
            .map(xnav -> xnav.text().get())
            .collect(Collectors.toSet());
        unlints.stream()
            .filter(unlint -> !present.contains(unlint))
            .forEach(
                unlint ->
                    xml.path(
                        String.format(
                            "program/metas/meta[head='unlint' and tail='%s']/@line", unlint
                        )
                        ).map(xnav -> xnav.text().get())
                        .collect(Collectors.toList())
                        .forEach(
                            line ->
                                defects.add(
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

    private Collection<String> existingDefects(final XML xmir) {
        final Collection<String> existing = new ListOf<>();
        this.lints.forEach(
            lint -> {
                try {
                    existing.addAll(
                        lint.defects(xmir).stream()
                            .map(Defect::rule)
                            .collect(Collectors.toList())
                    );
                } catch (final IOException exception) {
                    throw new IllegalStateException(exception);
                }
            }
        );
        return existing;
    }
}
