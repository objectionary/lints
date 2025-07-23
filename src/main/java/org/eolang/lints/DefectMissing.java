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
 * Is defect missing?
 * @since 0.0.44
 * @todo #671:45min Adjust `DefectMissing` to handle unlints with line ranges.
 *  Currently we don't support such line ranges in this and {@link LtUnlintNonExistingDefectWpa}
 *  lint, but we should, since {@link LtUnlint} supports them. If we will add the support of line
 *  ranges here, `unlint-non-existing-defect` in both scopes should catch up it. Don't forget to
 *  add tests for both scopes.
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
        final String[] split = unlint.split(":", -1);
        final String name = split[0];
        final List<Integer> lines = this.defects.get(name);
        if (unlint.matches(String.format("%s:\\d+-\\d+", name))) {
            missing = lines.stream().noneMatch(new UnlintInRange(unlint));
        } else {
            final Set<String> names;
            if (this.defects != null) {
                names = this.defects.keySet();
            } else {
                names = new SetOf<>();
            }
            if (split.length > 1) {
                missing = (!names.contains(name) || !lines.contains(Integer.parseInt(split[1])))
                    && !this.excluded.contains(name);
            } else {
                missing = !names.contains(name) && !this.excluded.contains(name);
            }
        }
        return missing;
    }
}
