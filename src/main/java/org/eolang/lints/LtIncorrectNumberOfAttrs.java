/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.github.lombrozo.xnav.Filter;
import com.github.lombrozo.xnav.Xnav;
import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.cactoos.io.ResourceOf;
import org.cactoos.list.ListOf;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;

/**
 * Lint to check incorrect number of attributes passed to the object in scope.
 *
 * @since 0.0.43
 */
final class LtIncorrectNumberOfAttrs implements Lint<Map<String, XML>> {

    @Override
    public Collection<Defect> defects(final Map<String, XML> pkg) throws IOException {
        final Collection<Defect> defects = new ArrayList<>(0);
        final Map<String, Integer> definitions = LtIncorrectNumberOfAttrs.objectDefinitions(pkg);
        pkg.forEach(
            (program, xmir) -> new Xnav(xmir.inner()).path("//o[@base and not(@base='∅')]")
                .forEach(
                    xnav -> {
                        final int provided = (int) xnav.elements(Filter.withName("o")).count();
                        final String object = xnav.attribute("base").text().orElse("unknown");
                        final Integer expected = definitions.get(object);
                        if (expected != null && provided != expected) {
                            defects.add(
                                new Defect.Default(
                                    this.name(),
                                    Severity.ERROR,
                                    program,
                                    Integer.parseInt(xnav.attribute("line").text().orElse("0")),
                                    String.format(
                                        "The object \"%s\" expects %d arguments, while %d provided",
                                        object, expected, provided
                                    )
                                )
                            );
                        }
                    }
                )
        );
        return defects;
    }

    @Override
    public String name() {
        return "incorrect-number-of-attributes";
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
     * Build object definitions.
     * @param pkg Package to scan
     * @return Map of object name and attributes count
     */
    private static Map<String, Integer> objectDefinitions(final Map<String, XML> pkg) {
        final Map<String, Integer> definitions = new HashMap<>(0);
        pkg.forEach(
            (program, xmir) -> {
                final Xnav xml = new Xnav(xmir.inner());
                xml.element("object")
                    .elements(Filter.withName("o")).forEach(
                        xob -> {
                            final List<Xnav> attrs = new ListOf<>();
                            xob.path("o[@base='∅']").forEach(attrs::add);
                            final String name = xob.attribute("name").text().orElse("unknown");
                            definitions.put(
                                LtIncorrectNumberOfAttrs.packagedFqn(name, xml), attrs.size()
                            );
                        }
                    );
            }
        );
        return definitions;
    }

    /**
     * Packaged FQN.
     * @param oname Object name
     * @param xml XML
     * @return Packaged FQN of object name
     */
    private static String packagedFqn(final String oname, final Xnav xml) {
        final String pack;
        final List<Xnav> packages = xml.element("object")
            .element("metas")
            .elements(Filter.withName("meta"))
            .filter(
                meta -> "package".equals(meta.element("head").text().get())
            )
            .collect(Collectors.toList());
        if (packages.isEmpty()) {
            pack = "org.eolang";
        } else {
            pack = packages.get(0).element("tail").text().get();
        }
        return String.format("Q.%s.%s", pack, oname);
    }
}
