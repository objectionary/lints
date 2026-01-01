/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import java.util.Collection;
import java.util.Collections;

/**
 * Lint that always returns a given defect.
 *
 * @since 0.0.35
 */
final class LtMono implements Lint<XML> {
    /**
     * The defect to return.
     */
    private final Defect defect;

    /**
     * Ctor.
     *
     * @param dft The defect to return
     */
    LtMono(final Defect dft) {
        this.defect = dft;
    }

    @Override
    public String name() {
        return this.defect.rule();
    }

    @Override
    public Collection<Defect> defects(final XML xmir) {
        return Collections.singleton(this.defect);
    }

    @Override
    public String motive() {
        throw new UnsupportedOperationException("#motive()");
    }
}
