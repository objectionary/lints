/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2025 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
        final Map<String, List<Xnav>> inverted = LtInconsistentArgs.basesOccurrences(whole);
        LtInconsistentArgs.mergedPrograms(whole).forEach(
            (base, counts) -> {
                if (counts.stream().distinct().count() != 1L) {
                    final List<Xnav> programs = inverted.get(base);
                    programs.forEach(
                        program ->
                            program.path(
                                String.format("//o[@base='%s']", base)
                                )
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

    private static Map<String, List<Xnav>> basesOccurrences(
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
