/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.github.lombrozo.xnav.Xnav;
import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.cactoos.io.ResourceOf;
import org.cactoos.list.ListOf;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;

/**
 * Lint for checking arguments inconsistency provided to the objects.
 *
 * @since 0.0.41
 */
final class LtInconsistentArgs implements Lint<Map<String, XML>> {

    @Override
    public String name() {
        return "inconsistent-args";
    }

    @Override
    public Collection<Defect> defects(final Map<String, XML> pkg) throws IOException {
        final Collection<Defect> defects = new LinkedList<>();
        final Map<Xnav, Map<String, List<Integer>>> whole = LtInconsistentArgs.scanUsages(pkg);
        final Map<String, List<Xnav>> bases = LtInconsistentArgs.baseOccurrences(whole);
        LtInconsistentArgs.mergedPrograms(whole).forEach(
            (base, counts) -> {
                if (counts.stream().distinct().count() != 1L) {
                    final List<Xnav> programs = bases.get(base);
                    programs.forEach(
                        program -> program.path(String.format("//o[@base='%s']", base))
                            .map(o -> Integer.parseInt(o.attribute("line").text().orElse("0")))
                            .forEach(
                                line ->
                                    defects.add(
                                        new Defect.Default(
                                            this.name(),
                                            Severity.WARNING,
                                            program.element("program").attribute("name")
                                                .text().orElse("unknown"),
                                            line,
                                            String.format(
                                                "Object '%s' has arguments inconsistency",
                                                base
                                            )
                                        )
                                    )
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
     * @param pkg Package with programs
     * @return Map of all object usages: program is the key, object name, arguments is the value.
     */
    private static Map<Xnav, Map<String, List<Integer>>> scanUsages(final Map<String, XML> pkg) {
        final Map<Xnav, Map<String, List<Integer>>> usages = new HashMap<>(0);
        pkg.values().forEach(
            xmir -> {
                final Map<String, List<Integer>> local = new HashMap<>(0);
                final Xnav program = new Xnav(xmir.inner());
                program.path("//o[@base]").forEach(
                    base -> {
                        final int args = base.node().getChildNodes().getLength();
                        local.computeIfAbsent(
                            base.attribute("base").text().get(),
                            k -> new ListOf<>()
                        ).add(args);
                    }
                );
                usages.put(program, local);
            }
        );
        return usages;
    }

    /**
     * Merge all object usages into single map.
     * @param whole All object usages across all programs
     * @return Merged object usages as a map
     */
    private static Map<String, List<Integer>> mergedPrograms(
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
     * Object occurrences across all programs, grouped by object base attribute.
     * @param whole All object usages across all programs.
     * @return Grouped base occurrences in the programs.
     */
    private static Map<String, List<Xnav>> baseOccurrences(
        final Map<Xnav, Map<String, List<Integer>>> whole
    ) {
        final Map<String, List<Xnav>> result = new HashMap<>(0);
        whole.forEach(
            (program, local) ->
                local.forEach(
                    (base, value) ->
                        result.computeIfAbsent(base, k -> new ListOf<>()).add(program)
                )
        );
        return result;
    }
}
