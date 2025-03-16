/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.github.lombrozo.xnav.Xnav;
import com.jcabi.xml.ClasspathSources;
import com.jcabi.xml.XML;
import com.jcabi.xml.XSL;
import com.jcabi.xml.XSLDocument;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.cactoos.io.ResourceOf;
import org.cactoos.io.UncheckedInput;
import org.cactoos.list.ListOf;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;

/**
 * All atom FQNs in the entire scope of programs must be unique.
 * This lint firstly transforms the original XMIR into XMIR that contains `@fqn`
 * attributes for each atom `o`, and then lints it.
 *
 * @since 0.0.31
 */
final class LtAtomIsNotUnique implements Lint<Map<String, XML>> {

    /**
     * Stylesheet for adding `@fqn` attribute for atoms.
     */
    private final XSL pre;

    /**
     * Ctor.
     */
    LtAtomIsNotUnique() {
        this(
            new XSLDocument(
                new UncheckedInput(
                    new ResourceOf("org/eolang/funcs/atom-fqns.xsl")
                ).stream()
            ).with(new ClasspathSources())
        );
    }

    /**
     * Ctor.
     *
     * @param sheet Sheet
     */
    LtAtomIsNotUnique(final XSL sheet) {
        this.pre = sheet;
    }

    @Override
    public String name() {
        return "atom-is-not-unique";
    }

    @Override
    public Collection<Defect> defects(final Map<String, XML> pkg) {
        final Collection<Defect> defects = new LinkedList<>();
        final Map<Xnav, List<String>> index = pkg.values().stream()
            .map(this.pre::transform)
            .map(xmir -> new Xnav(xmir.inner()))
            .collect(Collectors.toMap(Function.identity(), LtAtomIsNotUnique::fqns));
        final Collection<String> checked = new HashSet<>(0);
        index.forEach(
            (xmir, fqns) -> {
                fqns.stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                    .entrySet()
                    .stream()
                    .filter(entry -> entry.getValue() > 1L)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                    .forEach(
                        (aname, count) ->
                            IntStream.range(0, Math.toIntExact(count)).forEach(
                                pos -> defects.add(this.singleDefect(xmir, aname, pos))
                            )
                    );
                index.forEach(
                    (next, names) -> {
                        if (!Objects.equals(next, xmir)) {
                            final String pair = LtAtomIsNotUnique.pairHash(xmir, next);
                            if (!checked.contains(pair)) {
                                checked.add(pair);
                                names.stream()
                                    .filter(fqns::contains)
                                    .forEach(
                                        aname -> {
                                            defects.add(this.sharedDefect(next, xmir, aname));
                                            defects.add(this.sharedDefect(xmir, next, aname));
                                        }
                                    );
                            }
                        }
                    }
                );
            }
        );
        return defects;
    }

    @Override
    public String motive() {
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

    private Defect singleDefect(final Xnav xml, final String fqn, final int pos) {
        return new Defect.Default(
            this.name(),
            Severity.ERROR,
            xml.element("program").attribute("name").text().orElse("unknown"),
            Integer.parseInt(
                xml.path(
                    String.format("//o[@name='%s' and o[@name='λ']]", LtAtomIsNotUnique.oname(fqn))
                    )
                    .map(o -> o.attribute("line").text().get())
                    .collect(Collectors.toList()).get(pos)
            ),
            String.format("Atom \"%s\" is duplicated", fqn)
        );
    }

    private Defect sharedDefect(final Xnav xml, final Xnav original, final String fqn) {
        return new Defect.Default(
            this.name(),
            Severity.ERROR,
            xml.element("program").attribute("name").text().orElse("unknown"),
            Integer.parseInt(
                xml.path(
                    String.format("//o[@name='%s' and o[@name='λ']]", LtAtomIsNotUnique.oname(fqn))
                    )
                    .map(xnav -> xnav.attribute("line").text().orElse("0"))
                    .collect(Collectors.toList()).get(0)
            ),
            String.format(
                "Atom with FQN \"%s\" is duplicated, original was found in \"%s\"",
                fqn,
                original.element("program")
                    .attribute("name")
                    .text()
                    .orElse("unknown")
            )
        );
    }

    private static List<String> fqns(final Xnav xml) {
        final List<String> result;
        final List<String> fqns = xml.path("//o[@fqn]")
            .map(o -> o.attribute("fqn").text().get())
            .collect(Collectors.toList());
        if (
            xml.path("/program/metas/meta[head='package']").count() == 1L
        ) {
            final String pack = xml.path("/program/metas/meta[head='package']/tail")
                .map(o -> o.text().get())
                .findFirst().get();
            result = fqns.stream().map(fqn -> String.format("%s.%s", pack, fqn))
                .collect(Collectors.toList());
        } else {
            result = fqns;
        }
        return result.stream()
            .map(fqn -> String.format("Ф.%s", fqn))
            .collect(Collectors.toList());
    }

    private static String pairHash(final Xnav first, final Xnav second) {
        final String pair;
        if (first.hashCode() < second.hashCode()) {
            pair = String.format("%d:%d", first.hashCode(), second.hashCode());
        } else {
            pair = String.format("%d:%d", second.hashCode(), first.hashCode());
        }
        return pair;
    }

    private static String oname(final String fqn) {
        final String result;
        final List<String> parts = new ListOf<>(fqn.split("\\."));
        if (parts.size() > 1) {
            result = parts.get(parts.size() - 1);
        } else {
            result = parts.get(0);
        }
        return result;
    }
}
