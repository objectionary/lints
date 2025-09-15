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
 * @since 0.0.59
 */
final class LtRedundantHat implements Lint<XML> {
    @Override
    public Collection<Defect> defects(final XML xmir) throws IOException {
        final Collection<Defect> defects = new ArrayList<>(0);
        final Xnav xml = new Xnav(xmir.inner());
        final List<Xnav> objs = xml
            .path("//o[@base='^']")
            .collect(Collectors.toList());
        for (final Xnav obj : objs) {
            final String name = obj.attribute("name").text().orElse("");
            final List<Xnav> matches = xml
                .path(String.format("//o[@name='%s']", name))
                .collect(Collectors.toList());
            final List<Xnav> ancestors = obj
                .path(String.format("ancestor::o[@name='%s']", name))
                .collect(Collectors.toList());
            if (matches.size() == 1) {
                defects.add(
                    new Defect.Default(
                        this.name(),
                        Severity.WARNING,
                        new OnDefault(xmir).get(),
                        Integer.parseInt(obj.attribute("line").text().orElse("0")),
                        String.format(
                            "Redundant '^' notation: '%s' can be accessed without it",
                            name
                        )
                    )
                );
            } else if (!ancestors.isEmpty() && matches.size() > 1) {
                final Xnav target = ancestors.get(0);
                for (final Xnav match : matches) {
                    if (match.equals(target)) {
                        defects.add(
                            new Defect.Default(
                                this.name(),
                                Severity.WARNING,
                                new OnDefault(xmir).get(),
                                Integer.parseInt(obj.attribute("line").text().orElse("0")),
                                String.format(
                                    "Redundant '^' notation: '%s' resolves to the same object without it",
                                    name
                                )
                            )
                        );
                        break;
                    }
                }
            }
        }
        return defects;
    }

    @Override
    public String motive() throws IOException {
        return "The '^' notation is redundant and can be omitted for brevity.";
    }

    @Override
    public String name() {
        return "redundant-hat";
    }
}
