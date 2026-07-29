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
        return new Xnav(xmir.inner())
            .path("//o[@name]")
            .filter(this::isNotReserved)
            .map(this::toDefect)
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
     * Checks if the given object name is not reserved.
     * @param obj Xnav object
     * @return true if not reserved
     */
    private boolean isNotReserved(final Xnav obj) {
        final String name = obj.attribute("name").text().get();
        return !isLibraryName(name) && this.reserved.containsKey(name);
    }

    /**
     * Converts Xnav object to Defect.
     * @param obj Xnav object
     * @return Defect
     */
    private Defect toDefect(final Xnav obj) {
        final String name = obj.attribute("name").text().get();
        return new Defect.Default(
            this.name(),
            Severity.WARNING,
            new LineOf(obj).value(),
            String.format(
                "Object name \"%s\" is already reserved by object in the \"%s\"",
                name,
                this.reserved.get(name)
            )
        );
    }

    /**
     * Checks if the given name belongs to a standard library.
     * @param name Object name to check
     * @return True if it's a library name
     */
    private static boolean isLibraryName(final String name) {
        boolean result = false;
        if (name.startsWith("org.")) {
            result = true;
        } else if (name.startsWith("java.")) {
            result = true;
        } else if (name.startsWith("javax.")) {
            result = true;
        } else if (name.startsWith("junit.")) {
            result = true;
        } else if (name.startsWith("com.")) {
            result = true;
        } else if (name.startsWith("sun.")) {
            result = true;
        } else if (name.startsWith("jdk.")) {
            result = true;
        }
        return result;
    }
}
