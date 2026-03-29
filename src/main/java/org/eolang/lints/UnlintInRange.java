/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.google.common.base.Splitter;
import java.util.List;
import java.util.function.Predicate;
import org.cactoos.list.ListOf;

/**
 * Does unlint in the range?
 * @since 0.0.54
 */
final class UnlintInRange implements Predicate<Integer> {

    /**
     * The unlint expression.
     */
    private final String unlint;

    /**
     * Ctor.
     * @param unlt The unlint expression
     */
    UnlintInRange(final String unlt) {
        this.unlint = unlt;
    }

    @Override
    public boolean test(final Integer line) {
        return line >= Integer.parseInt(this.range().get(0))
            && line <= Integer.parseInt(this.range().get(1));
    }

    /**
     * Extract range from unlint expression.
     * @return List with two elements: start and end of range
     */
    private List<String> range() {
        return Splitter.on('-').splitToList(
            this.unlint.replace(
                String.format(
                    "%s:",
                    new ListOf<>(
                        Splitter.on(':').split(this.unlint.replace("+unlint", ""))
                    ).get(0)
                ),
                ""
            )
        );
    }
}
