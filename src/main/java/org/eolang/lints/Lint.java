/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.Collection;

/**
 * A single checker for an {@code .xmir} file.
 *
 * @since 0.0.1
 */
public interface Lint {

    /**
     * Name of the lint.
     * @return Lint name
     */
    String name();

    /**
     * Find and return defects.
     * @param xmir The XMIR to analyze
     * @return Defects
     */
    Collection<Defect> defects(XML xmir) throws IOException;

    /**
     * Returns motive for a lint, explaining why this lint exists.
     * @return Motive text about lint
     * @throws IOException if something went wrong
     */
    String motive() throws IOException;

}
