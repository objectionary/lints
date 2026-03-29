/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.github.lombrozo.xnav.Xnav;
import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.Collection;
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
        return pkg.values().stream()
            .flatMap(
                xmir -> pkg.values().stream()
                    .filter(oth -> !Objects.equals(oth, xmir))
                    .flatMap(oth -> this.duplicateDefects(xmir, oth).stream())
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
     * Find duplicate defects between two sources.
     * @param xmir Original source
     * @param oth Other source
     * @return Defects found
     */
    private Collection<Defect> duplicateDefects(final XML xmir, final XML oth) {
        return LtObjectIsNotUnique.sourceObjects(new Xnav(oth.inner())).entrySet().stream()
            .filter(
                object -> LtObjectIsNotUnique.containsDuplicate(
                    new Xnav(xmir.inner()),
                    new Xnav(oth.inner()),
                    object.getKey()
                )
            )
            .map(
                (Function<Map.Entry<String, String>, Defect>) object ->
                    new Defect.Default(
                        this.name(),
                        Severity.ERROR,
                        new ProgramName(oth).get(),
                        Integer.parseInt(object.getValue()),
                        String.format(
                            "The object name \"%s\" is not unique, original object was found in \"%s\"",
                            object.getKey(),
                            new ProgramName(xmir).get()
                        )
                    )
            )
            .collect(Collectors.toList());
    }

    private static boolean containsDuplicate(
        final Xnav original, final Xnav oth, final String name
    ) {
        return LtObjectIsNotUnique.sourceObjects(original).containsKey(name)
            && LtObjectIsNotUnique.packageName(oth)
            .equals(LtObjectIsNotUnique.packageName(original));
    }

    private static Map<String, String> sourceObjects(final Xnav xml) {
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
            name = xml.one("/object/metas/meta[head='package']/tail").text().get();
        } else {
            name = "";
        }
        return name;
    }
}
