/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.github.lombrozo.xnav.Filter;
import com.github.lombrozo.xnav.Xnav;
import com.jcabi.log.Logger;
import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;
import org.eolang.parser.ObjectName;

/**
 * Checks that `+alias` is pointing to existing `.xmir` files.
 *
 * @since 0.0.30
 */
final class LtIncorrectAlias implements Lint<Map<String, XML>> {

    @Override
    public String name() {
        return "incorrect-alias";
    }

    @Override
    public Collection<Defect> defects(final Map<String, XML> pkg) {
        final Collection<Defect> defects = new LinkedList<>();
        pkg.values().forEach(
            xmir -> {
                final Xnav xml = new Xnav(xmir.inner());
                final List<Xnav> aliased = xml.path("/object/metas/meta[head='alias']")
                    .collect(Collectors.toList());
                for (final Xnav alias : aliased) {
                    final String pointer = alias.element("tail").text().get();
                    final List<Xnav> parts = alias.elements(Filter.withName("part"))
                        .collect(Collectors.toList());
                    final String lookup = parts.get(parts.size() - 1).text().get().substring(2);
                    if (!pkg.containsKey(lookup)) {
                        defects.add(
                            new Defect.Default(
                                "incorrect-alias",
                                Severity.CRITICAL,
                                new ObjectName(xmir).get(),
                                Integer.parseInt(
                                    alias.attribute("line").text().orElse("0")
                                ),
                                Logger.format(
                                    "Alias \"%s\" points to \"%s\", but it's not in scope (%d): %[list]s",
                                    pointer, lookup, pkg.size(), pkg.keySet()
                                )
                            )
                        );
                    }
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
                    "org/eolang/motives/critical/incorrect-alias.md"
                )
            )
        ).asString();
    }
}
