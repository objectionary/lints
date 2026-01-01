/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import java.util.Collection;
import java.util.Collections;

/**
 * Lint that always complains.
 *
 * @since 0.0.1
 */
final class LtAlways implements Lint<XML> {

    @Override
    public String name() {
        return "always";
    }

    @Override
    public Collection<Defect> defects(final XML xmir) {
        return Collections.singleton(
            new Defect.Default(
                this.name(),
                Severity.ERROR,
                "noname",
                0,
                "always complains"
            )
        );
    }

    @Override
    public String motive() {
        return "";
    }

}
