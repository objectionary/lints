/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.github.lombrozo.xnav.Xnav;
import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Lint for reserved names.
 * @since 0.0.44
 */
final class LtReservedName implements Lint {

    /**
     * Reserved names.
     * The key is object name, the value is the path to EO file.
     */
    private final Map<String, String> reserved;

    /**
     * Ctor.
     */
    LtReservedName() {
        this(new ReservedNames());
    }

    /**
     * Ctor.
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
        return new Xnav(xmir.inner()).path("//o[@name]")
            .filter(
                object -> {
                    final String name = object.attribute("name").text().get();
                    return !isLibraryName(name)
                        && this.reserved.containsKey(name);
                }
            )
            .map(
                object -> new Defect.Default(
                    this.name(),
                    Severity.WARNING,
                    new LineOf(object).value(),
                    String.format(
                        "Object name \"%s\" is already reserved by object in the \"%s\"",
                        object.attribute("name").text().get(),
                        this.reserved.get(object.attribute("name").text().get())
                    )
                )
            )
            .collect(Collectors.toList());
    }

    @Override
    public String motive() throws IOException {
        return new MotiveFrom("names", this.name()).asString();
    }

    @Override
    public Fix fix() {
        return new FxEmpty();
    }

    /**
     * Checks if the given name belongs to a standard library.
     * @param name Object name to check
     * @return True if it's a library name
     */
    private static boolean isLibraryName(final String name) {
        if (name.startsWith("org.")) {
            return true;
        }
        if (name.startsWith("java.")) {
            return true;
        }
        if (name.startsWith("javax.")) {
            return true;
        }
        if (name.startsWith("junit.")) {
            return true;
        }
        if (name.startsWith("com.")) {
            return true;
        }
        if (name.startsWith("sun.")) {
            return true;
        }
        if (name.startsWith("jdk.")) {
            return true;
        }
        return false;
    }
}
