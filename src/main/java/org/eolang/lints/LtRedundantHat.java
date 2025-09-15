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
import java.util.List;
import java.util.stream.Collectors;
import org.eolang.parser.OnDefault;

/**
 * Lint that warns if a redundant {@code ^} is used.
 *
 * @since 0.0.60
 */
final class LtRedundantHat implements Lint<XML> {

    @Override
    public Collection<Defect> defects(final XML xmir) throws IOException {
        final Collection<Defect> defects = new ArrayList<>(0);
        final Xnav xml = new Xnav(xmir.inner());
        final List<Xnav> objects = xml.path("//o[@base='^']").collect(Collectors.toList());
        for (final Xnav object : objects) {
            final String name = object.attribute("name").text().get();
            final List<Xnav> parents = object.path(String.format("ancestor::o[.//o[@name='%s']]", name))
                .collect(Collectors.toList());
            if (parents.isEmpty()) {
                defects.add(
                    new Defect.Default(
                        this.name(),
                        Severity.WARNING,
                        new OnDefault(xmir).get(),
                        Integer.parseInt(object.attribute("line").text().orElse("0")),
                        String.format(
                            "Redundant '^' notation: '%s' can be accessed without it",
                            name
                        )
                    )
                );
            }
        }
        return defects;
    }

    @Override
    public String motive() throws IOException {
        return "The '^' notation is used when there are no naming conflicts, please omitted for brevity.";
    }

    @Override
    public String name() {
        return "redundant-hat";
    }
}

