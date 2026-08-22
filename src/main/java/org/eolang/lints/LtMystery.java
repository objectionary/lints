/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.github.lombrozo.xnav.Xnav;
import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Lint to catch mystery objects.
 *
 * <p>A mystery object is a free application of an object that is neither
 * declared anywhere in the program nor is one of the {@code org.eolang}
 * prime objects (like {@code number} or {@code bytes}). It almost always
 * means a typo in the name of an object or a missing {@code +alias} meta.
 * Such a call is compiled to {@code Φ.NAME} reference, which this lint
 * inspects.</p>
 *
 * @since 0.0.52
 */
final class LtMystery implements Lint {

    /**
     * Reserved names.
     * The key is object name, the value is the path to EO file.
     */
    private final Map<String, String> reserved;

    /**
     * Ctor.
     */
    LtMystery() {
        this(new ReservedNames());
    }

    /**
     * Ctor.
     * @param names Reserved names
     */
    LtMystery(final Map<String, String> names) {
        this.reserved = names;
    }

    @Override
    public String name() {
        return "mystery-object";
    }

    @Override
    public Collection<Defect> defects(final XML xmir) throws IOException {
        final Collection<Defect> defects;
        if (this.reserved.isEmpty()) {
            defects = new ArrayList<>(0);
        } else {
            defects = LtMystery.find(xmir, this.reserved);
        }
        return defects;
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
     * Detect mystery objects in the program.
     * @param xmir XMIR document
     * @param reserved Reserved org.eolang names
     * @return Detected defects
     */
    private static Collection<Defect> find(final XML xmir,
        final Map<String, String> reserved) {
        final Xnav xnav = new Xnav(xmir.inner());
        final Set<String> declared = xnav.path("//o[@name]").map(
            elem -> elem.attribute("name").text().get()
        ).collect(Collectors.toSet());
        return xnav.path("//o[@base]").filter(
            LtMystery::simpleBase
        ).filter(
            elem -> LtMystery.unknown(elem, declared, reserved)
        ).map(
            LtMystery::defect
        ).collect(Collectors.toList());
    }

    /**
     * Is the {@code @base} a bare single-object reference?
     * @param elem Object element
     * @return True if it's a simple object reference
     */
    private static boolean simpleBase(final Xnav elem) {
        final Optional<String> base = elem.attribute("base").text();
        return base.isPresent()
            && LtMystery.simple(base.get());
    }

    /**
     * Is the referenced object unknown?
     * The name is the {@code @base} value without the leading two characters,
     * which are the {@code Φ.} prefix.
     * @param elem Object element
     * @param declared Objects declared in the program
     * @param reserved Reserved org.eolang names
     * @return True if the object is neither declared nor reserved
     */
    private static boolean unknown(final Xnav elem,
        final Set<String> declared, final Map<String, String> reserved) {
        final String name = LtMystery.objectName(elem);
        return !declared.contains(name)
            && !reserved.containsKey(name);
    }

    /**
     * Object name from the {@code @base} attribute.
     * The name is the value without the leading two characters,
     * which are the {@code Φ.} prefix.
     * @param elem Object element
     * @return Object name
     */
    private static String objectName(final Xnav elem) {
        return elem.attribute("base").text().get().substring(2);
    }

    /**
     * Defect for an unknown object.
     * @param elem Object element
     * @return Defect
     */
    private static Defect defect(final Xnav elem) {
        return new Defect.Default(
            "mystery-object",
            Severity.ERROR,
            new LineOf(elem).value(),
            String.format(
                "Object \"%s\" is not defined in the program and is not part of org.eolang",
                LtMystery.objectName(elem)
            )
        );
    }

    /**
     * Check if the base is a reference to a single object, like
     * {@code Φ.bar}, and not to a path like {@code Φ.org.eolang.io.stdout}.
     * @param base Base attribute value
     * @return True if it's a simple object reference
     */
    private static boolean simple(final String base) {
        return base.matches("^Φ\\.[a-z][a-z0-9_-]*$");
    }
}
