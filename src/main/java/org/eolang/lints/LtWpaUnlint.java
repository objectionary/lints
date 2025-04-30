/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.log.Logger;
import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Lint that ignores linting if {@code +unlint} meta is present.
 *
 * @since 0.0.1
 */
final class LtWpaUnlint implements Lint<Map<String, XML>> {

    /**
     * The original lint.
     */
    private final Lint<Map<String, XML>> origin;

    /**
     * Ctor.
     * @param lint The lint to decorate
     */
    LtWpaUnlint(final Lint<Map<String, XML>> lint) {
        this.origin = lint;
    }

    @Override
    public String name() {
        return this.origin.name();
    }

    @Override
    public Collection<Defect> defects(final Map<String, XML> map) throws IOException {
        final Collection<Defect> defects = new ArrayList<>(0);
        for (final Defect defect : this.origin.defects(map)) {
            final XML xmir = map.get(defect.program());
            if (xmir == null) {
                throw new IllegalArgumentException(
                    Logger.format(
                        "The \"%s\" defect was found in \"%s\", but this source is not in scope (%[list]s), how come?",
                        defect.rule(), defect.program(), map.keySet()
                    )
                );
            }
            defects.addAll(new LtUnlint(new LtMono(defect)).defects(xmir));
        }
        return defects;
    }

    @Override
    public String motive() throws IOException {
        return this.origin.motive();
    }

}
