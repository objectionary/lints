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
import java.util.Map;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;
import org.eolang.parser.ObjectName;

/**
 * Lint for reserved names.
 *
 * @since 0.0.44
 */
final class LtReservedName implements Lint<XML> {

    /**
     * Reserved names.
     * The key is object name, the value is the path to EO file.
     */
    private final Map<String, String> reserved;

    /**
     * Ctor.
     */
    LtReservedName() throws Exception {
        this(new HomeReserved().value());
    }

    /**
     * Ctor.
     *
     * @param names Reserved names
     */
    LtReservedName(final Map<String, String> names) {
        this.reserved = names;
    }

    @Override
    public String name() {
        return "reserved-name";
    }

    @Override
    public Collection<Defect> defects(final XML xmir) throws IOException {
        final Collection<Defect> defects = new ArrayList<>(0);
        final Xnav source = new Xnav(xmir.inner());
        source.path("//o[@name]")
            .forEach(
                object -> {
                    final String oname = object.attribute("name").text().get();
                    if (this.reserved.keySet().contains(oname)) {
                        defects.add(
                            new Defect.Default(
                                this.name(),
                                Severity.WARNING,
                                new ObjectName(xmir).get(),
                                Integer.parseInt(object.attribute("line").text().orElse("0")),
                                String.format(
                                    "Object name \"%s\" is already reserved by object in the \"%s\"",
                                    oname, this.reserved.get(oname)
                                )
                            )
                        );
                    }
                }
            );
        return defects;
    }

    @Override
    public String motive() throws IOException {
        return new UncheckedText(
            new TextOf(
                new ResourceOf(
                    String.format(
                        "org/eolang/motives/names/%s.md", this.name()
                    )
                )
            )
        ).asString();
    }
}
