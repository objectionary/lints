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
        final String lint = new ListOf<>(
            Splitter.on(':').split(this.unlint.replace("+unlint", ""))
        ).get(0);
        final List<String> range = Splitter.on('-').splitToList(
            this.unlint.replace(String.format("%s:", lint), "")
        );
        return line >= Integer.parseInt(range.get(0)) && line <= Integer.parseInt(range.get(1));
    }
}
