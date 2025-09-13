/*
 * SPDX-FileCopyrightText: Copyright (c) 2025 Objectionary.com
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
 * Lint that warns if a comment is present at a test object.
 * @since 0.0.59
 */
final class LtTestComment implements Lint<XML> {
    @Override
    public Collection<Defect> defects(final XML xmir) throws IOException {
        final Collection<Defect> defects = new ArrayList<>(0);
        final Xnav xml = new Xnav(xmir.inner());
        final List<Xnav> objects = xml
            .path("/object//o[@name and starts-with(@name, '+')]")
            .collect(Collectors.toList());
        for (final Xnav object : objects) {
            final List<Xnav> comments = object
                .path("meta[@key='comment']")
                .collect(Collectors.toList());
            if (!comments.isEmpty()) {
                defects.add(
                    new Defect.Default(
                        "test-has-comment",
                        Severity.WARNING,
                        new OnDefault(xmir).get(),
                        Integer.parseInt(object.attribute("line").text().orElse("0")),
                        "Test object contains a comment. Prefer short, self-explanatory test names instead of documenting them."
                    )
                );
            }
        }
        return defects;
    }

    @Override
    public String motive() throws IOException {
        return "Comments in test objects are discouraged as they often duplicate the test name. Prefer short, clear test names.";
    }

    @Override
    public String name() {
        return "test-has-comment";
    }
}

