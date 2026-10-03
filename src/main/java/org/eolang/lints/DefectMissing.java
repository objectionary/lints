/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Is defect missing?
 *
 * @since 0.0.44
 */
final class DefectMissing implements Function<String, Boolean> {

    /**
     * Mapped defects.
     */
    private final Map<String, List<Integer>> defects;

    /**
     * Ctor.
     *
     * @param present Present defects
     */
    DefectMissing(final Map<String, List<Integer>> present) {
        this.defects = present;
    }

    @Override
    public Boolean apply(final String unlint) {
        final boolean missing;
        final String[] split = unlint.split(":", -1);
        final String name = split[0];
        final List<Integer> lines = this.defects.get(name);
        if (unlint.matches(String.format("%s:\\d+-\\d+", Pattern.quote(name)))) {
            if (lines == null) {
                missing = true;
            } else {
                missing = !lines.stream().anyMatch(new UnlintInRange(unlint));
            }
        } else {
            final Set<String> names = this.defects.keySet();
            if (split.length > 1) {
                missing = DefectMissing.missingAtLine(unlint, lines, names);
            } else {
                missing = !names.contains(name);
            }
        }
        return missing;
    }

    private static boolean missingAtLine(
        final String unlint,
        final List<Integer> lines,
        final Set<String> names
    ) {
        final String[] split = unlint.split(":", -1);
        final String name = split[0];
        boolean missing = !names.contains(name) || lines == null;
        if (!missing) {
            missing = !unlint.matches(
                String.format("%s:\\d+", Pattern.quote(name))
            );
            if (!missing) {
                try {
                    missing = !lines.contains(Integer.parseInt(split[1]));
                } catch (final NumberFormatException exception) {
                    missing = true;
                }
            }
        }
        return missing;
    }
}
