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
import org.cactoos.io.ResourceOf;
import org.cactoos.list.ListOf;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;
import org.eolang.parser.ObjectName;

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
final class LtInconsistentArgs implements Lint<Map<String, XML>> {

    @Override
    public String name() {
        return "inconsistent-args";
    }

    @Override
    public Collection<Defect> defects(final Map<String, XML> pkg) throws IOException {
        final Collection<Defect> defects = new ArrayList<>(0);
        final Map<Xnav, Map<String, List<Integer>>> whole = LtInconsistentArgs.scanUsages(pkg);
        final Map<String, List<Xnav>> bases = LtInconsistentArgs.baseOccurrences(whole);
        LtInconsistentArgs.mergedSources(whole).forEach(
            (base, counts) -> {
                if (counts.stream().distinct().count() != 1L) {
                    final List<Xnav> sources = bases.get(base);
                    final Map<String, List<Integer>> clashes = LtInconsistentArgs.clashes(
                        sources, base
                    );
                    sources.forEach(
                        src -> src.path(String.format("//o[@base='%s']", base))
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
                    base -> {
                        final int args = base.node().getChildNodes().getLength();
                        local.computeIfAbsent(
                            base.attribute("base").text().get(),
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
            src -> src.path(String.format("//o[@base='%s']", base))
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
}
