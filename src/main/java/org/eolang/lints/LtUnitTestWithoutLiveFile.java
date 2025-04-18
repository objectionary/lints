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
 * Unit test without live file.
 *
 * @since 0.0.30
 */
final class LtUnitTestWithoutLiveFile implements Lint<Map<String, XML>> {

    @Override
    public String name() {
        return "unit-test-without-live-file";
    }

    @Override
    public Collection<Defect> defects(final Map<String, XML> pkg) {
        final Collection<Defect> defects = new ArrayList<>(0);
        for (final String name : pkg.keySet()) {
            if (!name.endsWith("-test")) {
                continue;
            }
            final String live = name.replace("-test", "");
            if (pkg.containsKey(live)) {
                continue;
            }
            defects.add(
                new Defect.Default(
                    this.name(),
                    Severity.WARNING,
                    name,
                    0,
                    String.format(
                        "Live \".eo\" file \"%s\" was not found for \"%s\"", live, name
                    )
                )
            );
        }
        return defects;
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
