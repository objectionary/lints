/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.cactoos.Text;
import org.cactoos.list.ListOf;

/**
 * XPath expression to the void attribute reference.
 * @since 0.0.50
 */
final class VoidXpath implements Text {

    /**
     * Void FQN.
     */
    private final String fqn;

    /**
     * Ctor.
     * @param base Void FQN
     */
    VoidXpath(final String base) {
        this.fqn = base;
    }

    @Override
    public String asString() {
        final List<String> parts = new ListOf<>(this.fqn.split("\\."));
        final List<String> normalized = parts.subList(0, parts.size() - 1);
        final int rstart = normalized.indexOf("$");
        return String.format(
            "%s/o[@base='%s']",
            IntStream.range(0, rstart)
                .mapToObj(i -> String.format("o[@name='%s']", normalized.get(i)))
                .collect(Collectors.joining("/", "//", "")),
            IntStream.range(rstart + 1, normalized.size())
                .mapToObj(normalized::get)
                .collect(Collectors.joining(".", "$.", ""))
        ).replace("[@name=':anonymous']", "");
    }
}
