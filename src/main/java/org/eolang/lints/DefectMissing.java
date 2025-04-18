/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import org.cactoos.set.SetOf;

/**
 * Does defect missing.
 * @since 0.0.44
 */
final class DefectMissing implements Function<String, Boolean> {

    /**
     * Mapped defects.
     */
    private final Map<String, List<Integer>> defects;

    /**
     * Excluded lints.
     */
    private final Collection<String> excluded;

    /**
     * Ctor.
     * @param present Present defects
     * @param exld Excluded lints
     */
    DefectMissing(final Map<String, List<Integer>> present, final Collection<String> exld) {
        this.defects = present;
        this.excluded = exld;
    }

    @Override
    public Boolean apply(final String unlint) {
        final boolean missing;
        final String[] split = unlint.split(":");
        final String name = split[0];
        final Set<String> names;
        if (this.defects != null) {
            names = this.defects.keySet();
        } else {
            names = new SetOf<>();
        }
        if (split.length > 1) {
            final List<Integer> lines = this.defects.get(name);
            missing = (!names.contains(name) || !lines.contains(Integer.parseInt(split[1])))
                && !this.excluded.contains(name);
        } else {
            missing = !names.contains(name) && !this.excluded.contains(name);
        }
        return missing;
    }
}
