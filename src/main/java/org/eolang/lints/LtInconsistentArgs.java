/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.github.lombrozo.xnav.Xnav;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.cactoos.io.ResourceOf;
import org.cactoos.list.ListOf;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;
import org.eolang.parser.ObjectName;
import org.w3c.dom.Node;

/**
 * Lint for checking arguments inconsistency provided to the objects.
 *
 * @since 0.0.41
 * @todo #259:60min Optimize performance of inconsistent arguments finding.
 *  Instead of re-collecting objects in nested loops, we should merge all objects
 *  from all programs into single XMIR under '<o/>' element. After objects
 *  are merged, we can iterate over all the objects there only once, and find
 *  inconsistencies.
 */
@SuppressWarnings("PMD.TooManyMethods")
final class LtInconsistentArgs implements Lint<Map<String, XML>> {

    @Override
    public String name() {
        return "inconsistent-args";
    }

    @Override
    public Collection<Defect> defects(final Map<String, XML> pkg) throws IOException {
        final Collection<Defect> defects = new ArrayList<>(0);
        final Map<Xnav, Map<String, List<Integer>>> whole = LtInconsistentArgs.scanUsages(pkg);
        System.out.println(whole.values());
        final Map<String, List<Xnav>> bases = LtInconsistentArgs.baseOccurrences(whole);
        LtInconsistentArgs.mergedSources(whole).forEach(
            (base, counts) -> {
                if (counts.stream().distinct().count() != 1L) {
                    final List<Xnav> sources = bases.get(base);
                    final Map<String, List<Integer>> clashes = LtInconsistentArgs.clashes(
                        sources, base
                    );
                    sources.forEach(
                        src ->
                            src.path(
                                LtInconsistentArgs.fqnToXpath(base, src)
                            )
//                            .filter(
//                                o ->
//                                    !LtInconsistentArgs.voidAttribute(
//                                        LtInconsistentArgs.relativizeToTopObject(base, src), o
//                                    ) && !LtInconsistentArgs.objectReference(o)
//                            )
                            .forEach(
                                o -> {
                                    final String current = new ObjectName(
                                        new XMLDocument(src.node())
                                    ).get();
                                    final int cline = Integer.parseInt(
                                        o.attribute("line").text().orElse("0")
                                    );
                                    defects.add(
                                        new Defect.Default(
                                            this.name(),
                                            Severity.WARNING,
                                            current,
                                            cline,
                                            String.format(
                                                "Object '%s' has arguments inconsistency (the usage clashes with [%s])",
                                                base,
                                                LtInconsistentArgs.objectClashes(
                                                    clashes, current, cline
                                                )
                                            )
                                        )
                                    );
                                }
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
                        "org/eolang/motives/misc/%s.md", this.name()
                    )
                )
            )
        ).asString();
    }

    /**
     * Scan all usages across package.
     * @param pkg Package with sources
     * @return Map of all object usages: source is the key, object name, arguments is the value.
     */
    private static Map<Xnav, Map<String, List<Integer>>> scanUsages(final Map<String, XML> pkg) {
        final Map<Xnav, Map<String, List<Integer>>> usages = new HashMap<>(0);
        pkg.values().forEach(
            xmir -> {
                final Map<String, List<Integer>> local = new HashMap<>(0);
                final Xnav source = new Xnav(xmir.inner());
                source.path("//o[@base]").forEach(
                    o -> {
                        final int args = o.node().getChildNodes().getLength();
                        final String base = o.attribute("base").text().get();
                        final String ref;
                        if (base.startsWith("$.")) {
                            if (LtInconsistentArgs.voidAttribute(base, o)) {
                                ref = LtInconsistentArgs.voidFqn(base, o, source);
                            } else {
                                ref = String.format("%s.%s", new ObjectName(source).get(), base);
                            }
                        } else {
                            ref = base;
                        }
                        local.computeIfAbsent(
                            ref,
                            k -> new ListOf<>()
                        ).add(args);
                    }
                );
                usages.put(source, local);
            }
        );
        return usages;
    }

    /**
     * Base refers to void attribute?
     * @param base Object base
     * @param object Object
     * @return True or False
     */
    private static boolean voidAttribute(final String base, final Xnav object) {
        final Xnav method = LtInconsistentArgs.parentObject(object);
        return method.path(String.format("o[@name='%s']", base.replace("$.", ""))).anyMatch(
            attr -> attr.attribute("base").text().filter("∅"::equals).isPresent()
        );
    }

    private static String voidFqn(final String base, final Xnav object, final Xnav source) {
        return String.format(
            "%s%s.∅",
            LtInconsistentArgs.parentTree(
                LtInconsistentArgs.parentObject(object), new ObjectName(source).get()
            ),
            base
        );
    }

    private static String parentTree(final Xnav object, final String top) {
        final List<String> tree = new ListOf<>(top);
        Xnav parent = LtInconsistentArgs.parentObject(object);
        if (!"object".equals(parent.node().getNodeName())) {
            while (!parent.attribute("name").text().get().equals(top)) {
                tree.add(parent.attribute("name").text().get());
                parent = LtInconsistentArgs.parentObject(parent);
            }
        }
        return tree.stream().collect(Collectors.joining(".", "", "."));
    }

    private static String fqnToXpath(final String fnq, final Xnav src) {
        // main.$.x.∅
        //o[@name='main']/o[@base='$.x]
        final String xpath;
        if (fnq.endsWith("∅")) {
            // todo build from full tree
            xpath = "//o[@name='main']/o[@base='$.x']";
        } else {
            xpath = String.format(
                "//o[@base='%s']", LtInconsistentArgs.relativizeToTopObject(fnq, src)
            );
        }
        return xpath;
    }

    /**
     * Object is a reference to itself?
     * @param object Object
     * @return True or False
     */
    private static boolean objectReference(final Xnav object) {
        final Optional<String> base = object.attribute("base").text();
        return object.attribute("name").text().isEmpty() && base.isPresent()
            && base.get().startsWith("$.");
    }

    /**
     * Merge all object usages into single map.
     * @param whole All object usages across all sources
     * @return Merged object usages as a map
     */
    private static Map<String, List<Integer>> mergedSources(
        final Map<Xnav, Map<String, List<Integer>>> whole
    ) {
        final Map<String, List<Integer>> merged = new HashMap<>(0);
        whole.forEach(
            (xnav, localized) ->
                localized.forEach(
                    (base, counts) ->
                        merged.computeIfAbsent(base, k -> new ListOf<>()).addAll(counts)
                )
        );
        return merged;
    }

    /**
     * Object occurrences across all sources, grouped by object base attribute.
     * @param whole All object usages across all sources
     * @return Grouped base occurrences in the sources
     */
    private static Map<String, List<Xnav>> baseOccurrences(
        final Map<Xnav, Map<String, List<Integer>>> whole
    ) {
        final Map<String, List<Xnav>> result = new HashMap<>(0);
        whole.forEach(
            (src, local) ->
                local.forEach(
                    (base, value) ->
                        result.computeIfAbsent(base, k -> new ListOf<>()).add(src)
                )
        );
        return result;
    }

    /**
     * Aggregate usage clashes for the given base.
     * @param sources Sources
     * @param base Base
     * @return Usage clashes
     */
    private static Map<String, List<Integer>> clashes(
        final Iterable<Xnav> sources, final String base
    ) {
        final Map<String, List<Integer>> clashes = new HashMap<>(16);
        sources.forEach(
            src ->
                src.path(
                    LtInconsistentArgs.fqnToXpath(base, src)
                )
                .filter(
                    o -> !LtInconsistentArgs.voidAttribute(
                        LtInconsistentArgs.relativizeToTopObject(base, src), o
                    ) && !LtInconsistentArgs.objectReference(o)
                )
                .forEach(
                    o -> {
                        final String program = new ObjectName(new XMLDocument(src.node())).get();
                        final int line = Integer.parseInt(o.attribute("line").text().orElse("0"));
                        clashes.computeIfAbsent(program, k -> new ListOf<>()).add(line);
                    }
                )
        );
        return clashes;
    }

    /**
     * List of clashes for given object.
     * @param clashes List of clashes
     * @param current Current object
     * @param oline Origin line in given object
     * @return With which objects, given object clashes, as string expression
     */
    private static String objectClashes(
        final Map<String, List<Integer>> clashes, final String current, final int oline
    ) {
        final Collection<String> others = new ListOf<>();
        clashes.forEach(
            (program, lines) ->
                lines.forEach(
                    line -> {
                        if (!(program.equals(current) && line == oline)) {
                            others.add(String.format("%s:%d", program, line));
                        }
                    }
                )
        );
        return String.join(", ", others);
    }

    /**
     * Relativize base to the top object name.
     * @param base Object base
     * @param source Source
     * @return Relativized object base
     */
    private static String relativizeToTopObject(final String base, final Xnav source) {
        final String top = new ObjectName(source).get();
        final String result;
        if (base.startsWith(String.format("%s.$.", top))) {
            result = base.replace(String.format("%s.", new ObjectName(source).get()), "");
        } else {
            result = base;
        }
        return result;
    }

    private static Xnav parentObject(final Xnav object) {
        final Xnav result;
        final Node prev = object.node().getParentNode();
        if (prev != null && (int) prev.getNodeType() == (int) Node.ELEMENT_NODE) {
            result = new Xnav(prev);
        } else {
            result = new Xnav("<o/>");
        }
        return result;
    }
}
