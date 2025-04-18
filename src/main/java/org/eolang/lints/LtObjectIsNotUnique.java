/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.github.lombrozo.xnav.Xnav;
import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;

/**
 * Object is not unique.
 *
 * @since 0.0.30
 */
final class LtObjectIsNotUnique implements Lint<Map<String, XML>> {

    @Override
    public String name() {
        return "object-is-not-unique";
    }

    @Override
    public Collection<Defect> defects(final Map<String, XML> pkg) {
        final Collection<Defect> defects = new LinkedList<>();
        for (final XML xmir : pkg.values()) {
            final Xnav xml = new Xnav(xmir.inner());
            final String src = xml.element("object").element("o")
                .attribute("name").text().orElse("unknown");
            for (final XML oth : pkg.values()) {
                final Xnav second = new Xnav(oth.inner());
                if (Objects.equals(oth, xmir)) {
                    continue;
                }
                LtObjectIsNotUnique.programObjects(second).entrySet().stream()
                    .filter(
                        object ->
                            LtObjectIsNotUnique.containsDuplicate(xml, second, object.getKey())
                    )
                    .map(
                        (Function<Map.Entry<String, String>, Defect>) object ->
                            new Defect.Default(
                                this.name(),
                                Severity.ERROR,
                                second.element("object")
                                    .element("o")
                                    .attribute("name")
                                    .text()
                                    .orElse("unknown"),
                                Integer.parseInt(object.getValue()),
                                String.format(
                                    "The object name \"%s\" is not unique, original object was found in \"%s\"",
                                    object.getKey(), src
                                )
                            )
                    )
                    .forEach(defects::add);
            }
        }
        return defects;
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

    private static boolean containsDuplicate(
        final Xnav original, final Xnav oth, final String name
    ) {
        return LtObjectIsNotUnique.programObjects(original).containsKey(name)
            && LtObjectIsNotUnique.packageName(oth)
            .equals(LtObjectIsNotUnique.packageName(original));
    }

    private static Map<String, String> programObjects(final Xnav xml) {
        final List<String> names = xml.path("/object/o/@name")
            .map(oname -> oname.text().get())
            .collect(Collectors.toList());
        return IntStream.range(0, names.size())
            .boxed()
            .collect(
                Collectors.toMap(
                    names::get,
                    pos ->
                        xml.path(String.format("/object/o[%d]/@line", pos + 1))
                            .findFirst().flatMap(Xnav::text).orElse("0"),
                    (existing, replacement) -> replacement
                )
            );
    }

    private static String packageName(final Xnav xml) {
        final String name;
        if (
            xml.path("/object/metas/meta[head='package']").count() == 1L
        ) {
            name = xml.path("/object/metas/meta[head='package']/tail")
                .findFirst().get().text().get();
        } else {
            name = "";
        }
        return name;
    }
}
