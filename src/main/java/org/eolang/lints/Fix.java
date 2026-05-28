/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import java.io.IOException;

/**
 * A fix for a lint defect.
 * @since 0.2.1
 */
public interface Fix {

    /**
     * Apply the fix and return the corrected XMIR.
     * @param xmir The XMIR to fix
     * @return Fixed XMIR
     * @throws IOException If something goes wrong
     */
    XML apply(XML xmir) throws IOException;
}
