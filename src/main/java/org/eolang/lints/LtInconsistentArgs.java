/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.github.lombrozo.xnav.Xnav;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.cactoos.io.ResourceOf;
import org.cactoos.list.ListOf;
import org.cactoos.map.MapOf;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;
import org.w3c.dom.Node;

/**
 * Lint for checking arguments' inconsistency provided to the objects.
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
        final Map<Xnav, Map<String, List<Integer>>> whole = LtInconsistentArgs.scanUsages(pkg);
        final Map<String, List<Xnav>> bases = LtInconsistentArgs.baseOccurrences(whole);
        return LtInconsistentArgs.mergedSources(whole).entrySet().stream()
            .filter(entry -> entry.getValue().stream().distinct().count() != 1L)
            .flatMap(
                entry -> bases.get(entry.getKey()).stream().flatMap(
                    src -> this.findClashDefects(
                        entry.getKey(), src, bases.get(entry.getKey())
                    )
                )
            )
            .collect(Collectors.toList());
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
     * Find clash defects for a single source.
     * @param base Base object name
     * @param src Source XMIR navigator
     * @param sources All sources containing this base
     * @return Stream of defects
     */
    private Stream<Defect> findClashDefects(
        final String base,
        final Xnav src,
        final List<Xnav> sources
    ) {
        final Map<String, List<Integer>> clashes = LtInconsistentArgs.clashes(sources, base);
        final String program = new ProgramName(new XMLDocument(src.node())).get();
        return LtInconsistentArgs.fqnToSearch(base, src).entrySet().stream()
            .flatMap(
                entry -> src.path(entry.getKey())
                    .filter(o -> !LtInconsistentArgs.objectReference(o))
                    .filter(entry.getValue())
                    .map(
                        o -> {
                            final int line = Integer.parseInt(
                                o.attribute("line").text().orElse("0")
                            );
                            return new Defect.Default(
                                this.name(),
                                Severity.WARNING,
                                program,
                                line,
                                String.format(
                                    "Object '%s' has arguments inconsistency (clashes with [%s])",
                                    base,
                                    LtInconsistentArgs.objectClashes(clashes, program, line)
                                )
                            );
                        }
                    )
            );
    }

    /**
     * Scan all usages across package.
     * @param pkg Package with sources
     * @return Map of all object usages: source is the key, object name, arguments is the value.
     */
    private static Map<Xnav, Map<String, List<Integer>>> scanUsages(final Map<String, XML> pkg) {
        return pkg.values().stream()
            .map(xmir -> new Xnav(xmir.inner()))
            .collect(
                Collectors.toMap(
                    source -> source,
                    source -> source.path("//o[@base]")
                        .collect(
                            Collectors.groupingBy(
                                o -> LtInconsistentArgs.objectRef(o, source),
                                Collectors.mapping(
                                    o -> o.node().getChildNodes().getLength(),
                                    Collectors.toList()
                                )
                            )
                        )
                )
            );
    }

    /**
     * Get object reference for base attribute.
     * @param obj Object navigator
     * @param source Source navigator
     * @return Reference string
     */
    private static String objectRef(final Xnav obj, final Xnav source) {
        final String base = obj.attribute("base").text().get();
        final String result;
        if (base.startsWith("ξ.") && LtInconsistentArgs.voidAttribute(base, obj)) {
            result = LtInconsistentArgs.voidFqn(base, obj);
        } else if (base.startsWith("ξ.")) {
            result = String.format(
                "%s.%s",
                new ProgramName(new XMLDocument(source.node())).get(),
                base
            );
        } else {
            result = base;
        }
        return result;
    }

    /**
     * Merge all object usages into single map.
     * @param whole All object usages across all sources
     * @return Merged object usages as a map
     */
    private static Map<String, List<Integer>> mergedSources(
        final Map<Xnav, Map<String, List<Integer>>> whole
    ) {
        return whole.values().stream()
            .flatMap(localized -> localized.entrySet().stream())
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> new ListOf<>(entry.getValue()),
                    (list1, list2) -> {
                        list1.addAll(list2);
                        return list1;
                    }
                )
            );
    }

    /**
     * Object occurrences across all sources, grouped by object base attribute.
     * @param whole All object usages across all sources
     * @return Grouped base occurrences in the sources
     */
    private static Map<String, List<Xnav>> baseOccurrences(
        final Map<Xnav, Map<String, List<Integer>>> whole
    ) {
        return whole.entrySet().stream()
            .flatMap(
                entry -> entry.getValue().keySet().stream()
                    .map(base -> Map.entry(base, entry.getKey()))
            )
            .collect(
                Collectors.groupingBy(
                    Map.Entry::getKey,
                    Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                )
            );
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
        return java.util.stream.StreamSupport.stream(sources.spliterator(), false)
            .flatMap(src -> LtInconsistentArgs.sourceClashes(src, base))
            .collect(
                Collectors.groupingBy(
                    Map.Entry::getKey,
                    Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                )
            );
    }

    /**
     * Find clashes from a single source.
     * @param src Source navigator
     * @param base Base name
     * @return Stream of program-line entries
     */
    private static Stream<Map.Entry<String, Integer>> sourceClashes(
        final Xnav src, final String base
    ) {
        return LtInconsistentArgs.fqnToSearch(base, src).entrySet().stream()
            .flatMap(
                entry -> src.path(entry.getKey())
                    .filter(o -> !LtInconsistentArgs.objectReference(o))
                    .filter(entry.getValue())
                    .map(
                        o -> Map.entry(
                            new ProgramName(new XMLDocument(src.node())).get(),
                            Integer.parseInt(o.attribute("line").text().orElse("0"))
                        )
                    )
            );
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
        return clashes.entrySet().stream()
            .flatMap(
                entry -> entry.getValue().stream()
                    .filter(line -> !(entry.getKey().equals(current) && line == oline))
                    .map(line -> String.format("%s:%d", entry.getKey(), line))
            )
            .collect(Collectors.joining(", "));
    }

    /**
     * Base refers to void attribute?
     * @param base Object base
     * @param object Object
     * @return True or False
     */
    private static boolean voidAttribute(final String base, final Xnav object) {
        return LtInconsistentArgs.parentObject(object)
            .path(String.format("o[@name='%s']", base.replace("ξ.", "")))
            .anyMatch(attr -> attr.attribute("base").text().filter("∅"::equals).isPresent());
    }

    /**
     * Object is a reference to itself?
     * @param object Object
     * @return True or False
     */
    private static boolean objectReference(final Xnav object) {
        final Optional<String> base = object.attribute("base").text();
        return object.attribute("name").text().isEmpty() && base.isPresent()
            && base.get().startsWith("ξ.");
    }

    /**
     * Void FQN for given base in object scope.
     * @param base Base
     * @param object Object
     * @return Void FQN
     */
    private static String voidFqn(final String base, final Xnav object) {
        final Xnav method = LtInconsistentArgs.parentObject(object);
        return String.format(
            "%s%s.%s.∅",
            LtInconsistentArgs.parentTree(
                method
            ),
            LtInconsistentArgs.coordinates(method),
            base
        );
    }

    /**
     * Parent tree up to the given object, in string.
     * @param object Object up to build the tree
     * @return Parent tree
     */
    private static String parentTree(final Xnav object) {
        final List<String> tree = new ListOf<>();
        Xnav current = LtInconsistentArgs.parentObject(object);
        while (!"object".equals(current.node().getNodeName())) {
            tree.add(LtInconsistentArgs.coordinates(current));
            current = LtInconsistentArgs.parentObject(current);
        }
        final String result;
        if (tree.isEmpty()) {
            result = "";
        } else {
            result = tree.stream().collect(Collectors.joining(".", "", "."));
        }
        return result;
    }

    /**
     * Map object FQN to search.
     * @param fqn Object FQN
     * @param src Source XMIR
     * @return Search map, where key is XPath, value is search filter
     */
    private static Map<String, Predicate<Xnav>> fqnToSearch(final String fqn, final Xnav src) {
        final Map<String, Predicate<Xnav>> result = new MapOf<>();
        if (fqn.endsWith("∅")) {
            result.put(new VoidXpath(fqn).asString(), object -> true);
        } else {
            result.put(
                String.format(
                    "//o[@base='%s']", LtInconsistentArgs.relativizeToTopObject(fqn, src)
                ),
                object ->
                    !LtInconsistentArgs.voidAttribute(
                        LtInconsistentArgs.relativizeToTopObject(fqn, src), object
                    )
            );
        }
        return result;
    }

    /**
     * Relativize base to the top object name.
     * @param base Object base
     * @param source Source
     * @return Relativized object base
     */
    private static String relativizeToTopObject(final String base, final Xnav source) {
        final String top = new ProgramName(new XMLDocument(source.node())).get();
        final String result;
        if (base.startsWith(String.format("%s.ξ.", top))) {
            result = base.replace(String.format("%s.", top), "");
        } else {
            result = base;
        }
        return result;
    }

    /**
     * Object coordinates.
     * @param object Object
     * @return Object coordinates
     */
    private static String coordinates(final Xnav object) {
        final String result;
        if (object.attribute("name").text().isPresent()) {
            result = object.attribute("name").text().get();
        } else {
            result = ":anonymous";
        }
        return result;
    }

    /**
     * Parent of the given object.
     * @param object Current object
     * @return Parent object
     */
    private static Xnav parentObject(final Xnav object) {
        final Xnav result;
        final Node prev = object.node().getParentNode();
        if (prev != null && prev.getNodeType() == Node.ELEMENT_NODE) {
            result = new Xnav(prev);
        } else {
            result = new Xnav("<o/>");
        }
        return result;
    }
}
