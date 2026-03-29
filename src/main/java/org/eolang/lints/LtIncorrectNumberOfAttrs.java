/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.github.lombrozo.xnav.Filter;
import com.github.lombrozo.xnav.Xnav;
import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;

/**
 * Lint to check incorrect number of attributes passed to the object in scope.
 *
 * @since 0.0.43
 */
final class LtIncorrectNumberOfAttrs implements Lint<Map<String, XML>> {

    @Override
    public String name() {
        return "incorrect-number-of-attributes";
    }

    @Override
    public Collection<Defect> defects(final Map<String, XML> pkg) throws IOException {
        return pkg.entrySet().stream()
            .flatMap(
                entry -> this.sourceDefects(
                    entry.getKey(),
                    entry.getValue(),
                    LtIncorrectNumberOfAttrs.objectDefinitions(pkg)
                ).stream()
            )
            .collect(Collectors.toList());
    }

    @Override
    public String motive() throws IOException {
        return new UncheckedText(
            new TextOf(
                new ResourceOf(
                    String.format(
                        "org/eolang/motives/errors/%s.md", this.name()
                    )
                )
            )
        ).asString();
    }

    /**
     * Find defects for single source.
     * @param program Program name
     * @param xmir Source XML
     * @param definitions Object definitions
     * @return Defects found
     */
    private Collection<Defect> sourceDefects(
        final String program,
        final XML xmir,
        final Map<String, Integer> definitions
    ) {
        return new Xnav(xmir.inner()).path("//o[@base and not(@base='∅')]")
            .filter(
                xnav -> {
                    final Integer expected = definitions.get(
                        xnav.attribute("base").text().orElse("unknown")
                    );
                    return expected != null
                        && (int) xnav.elements(Filter.withName("o")).count() != expected;
                }
            )
            .map(xnav -> this.objectDefect(program, xnav, definitions))
            .collect(Collectors.toList());
    }

    /**
     * Create defect for incorrect number of attributes.
     * @param program Program name
     * @param xnav Object navigator
     * @param definitions Object definitions
     * @return Defect
     */
    private Defect objectDefect(
        final String program, final Xnav xnav, final Map<String, Integer> definitions
    ) {
        return new Defect.Default(
            this.name(),
            Severity.ERROR,
            program,
            Integer.parseInt(xnav.attribute("line").text().orElse("0")),
            String.format(
                "The object \"%s\" usually expects %d arguments, while %d provided here",
                xnav.attribute("base").text().orElse("unknown"),
                definitions.get(xnav.attribute("base").text().orElse("unknown")),
                (int) xnav.elements(Filter.withName("o")).count()
            )
        );
    }

    /**
     * Build object definitions.
     * @param pkg Package to scan
     * @return Map of object name and attributes count
     */
    private static Map<String, Integer> objectDefinitions(final Map<String, XML> pkg) {
        return pkg.values().stream()
            .flatMap(
                xmir -> new Xnav(xmir.inner()).element("object")
                    .elements(Filter.withName("o"))
                    .map(xob -> new ObjectDef(xmir, xob))
            )
            .collect(Collectors.toMap(ObjectDef::fqn, ObjectDef::attrCount, (a, b) -> a));
    }

    /**
     * Packaged FQN.
     * @param oname Object name
     * @param xml XML
     * @return Packaged FQN of object name
     */
    private static String packagedFqn(final String oname, final Xnav xml) {
        final List<Xnav> packages = xml.element("object")
            .element("metas")
            .elements(Filter.withName("meta"))
            .filter(
                meta -> "package".equals(meta.element("head").text().get())
            )
            .collect(Collectors.toList());
        final String result;
        if (packages.isEmpty()) {
            result = String.format("Φ.%s", oname);
        } else {
            result = String.format("Φ.%s.%s", packages.get(0).element("tail").text().get(), oname);
        }
        return result;
    }

    /**
     * Object definition helper.
     * @since 0.0.43
     */
    private static final class ObjectDef {

        /**
         * Source XMIR.
         */
        private final XML xmir;

        /**
         * Object navigator.
         */
        private final Xnav xob;

        /**
         * Ctor.
         * @param xmr XMIR source
         * @param obj Object navigator
         */
        ObjectDef(final XML xmr, final Xnav obj) {
            this.xmir = xmr;
            this.xob = obj;
        }

        /**
         * Get fully qualified name.
         * @return FQN
         */
        String fqn() {
            return LtIncorrectNumberOfAttrs.packagedFqn(
                this.xob.attribute("name").text().orElse("unknown"),
                new Xnav(this.xmir.inner())
            );
        }

        /**
         * Count attributes.
         * @return Attribute count
         */
        int attrCount() {
            return (int) this.xob.path("o[@base='∅']").count();
        }
    }
}
