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
 *
 * @since 0.0.54
 */
final class UnlintInRange implements Predicate<Integer> {

    /**
     * The unlint expression.
     */
    private final String unlint;

    /**
     * Ctor.
     *
     * @param unlt The unlint expression
     */
    UnlintInRange(final String unlt) {
        this.unlint = unlt;
    }

    @Override
    public boolean test(final Integer line) {
        final List<String> range = this.range();
        return UnlintInRange.fitsInt(range.get(0))
            && UnlintInRange.fitsInt(range.get(1))
            && line >= Integer.parseInt(range.get(0))
            && line <= Integer.parseInt(range.get(1));
    }

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

    private static boolean fitsInt(final String value) {
        boolean result;
        try {
            Integer.parseInt(value);
            result = true;
        } catch (final NumberFormatException exception) {
            result = false;
        }
        return result;
    }
}
