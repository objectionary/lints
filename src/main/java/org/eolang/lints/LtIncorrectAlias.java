/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.github.lombrozo.xnav.Filter;
import com.github.lombrozo.xnav.Xnav;
import com.jcabi.log.Logger;
import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;

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
        return pkg.values().stream()
            .flatMap(
                xmir -> new Xnav(xmir.inner())
                    .path("/object/metas/meta[head='alias']")
                    .filter(alias -> !pkg.containsKey(this.lookupName(alias)))
                    .map(alias -> this.aliasDefect(alias, xmir, pkg))
            )
            .collect(Collectors.toList());
    }

    /**
     * Extract lookup name from alias.
     * @param alias Alias navigator
     * @return Lookup name
     */
    private String lookupName(final Xnav alias) {
        final List<Xnav> parts = alias.elements(Filter.withName("part"))
            .collect(Collectors.toList());
        return parts.get(parts.size() - 1).text().get().substring(2);
    }

    /**
     * Create defect for incorrect alias.
     * @param alias Alias navigator
     * @param xmir Source XML
     * @param pkg Package map
     * @return Defect
     */
    private Defect aliasDefect(final Xnav alias, final XML xmir, final Map<String, XML> pkg) {
        return new Defect.Default(
            "incorrect-alias",
            Severity.CRITICAL,
            new ProgramName(xmir).get(),
            Integer.parseInt(alias.attribute("line").text().orElse("0")),
            Logger.format(
                "Alias \"%s\" points to \"%s\", but it's not in scope (%d): %[list]s",
                alias.element("tail").text().get(),
                this.lookupName(alias),
                pkg.size(),
                pkg.keySet()
            )
        );
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
