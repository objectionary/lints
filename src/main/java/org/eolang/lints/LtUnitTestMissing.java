/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.IoCheckedText;
import org.cactoos.text.TextOf;

/**
 * A test is missing for a live EO source.
 *
 * @since 0.1.0
 */
final class LtUnitTestMissing implements Lint<Map<String, XML>> {

    @Override
    public Collection<Defect> defects(final Map<String, XML> pkg) throws IOException {
        final Collection<Defect> defects = new ArrayList<>(0);
        for (final String name : pkg.keySet()) {
            if (name.endsWith("-tests")) {
                continue;
            }
            final String lookup = String.format("%s-tests", name);
            if (pkg.containsKey(lookup)) {
                continue;
            }
            defects.add(
                new Defect.Default(
                    "unit-test-missing",
                    Severity.WARNING,
                    name,
                    0,
                    String.format("Tests \"%s\" not found for \"%s\"", lookup, name)
                )
            );
        }
        return defects;
    }

    @Override
    public String name() {
        return "unit-test-missing";
    }

    @Override
    public String motive() throws IOException {
        return new IoCheckedText(
            new TextOf(
                new ResourceOf(
                    String.format(
                        "org/eolang/motives/units/%s.md", this.name()
                    )
                )
            )
        ).asString();
    }
}
