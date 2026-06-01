/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;

/**
 * A no-op fix that returns the XMIR unchanged.
 * Used by lints that do not support auto-fixing.
 * @since 0.2.1
 */
public final class FxEmpty implements Fix {

    @Override
    public XML apply(final XML xmir) {
        return xmir;
    }
}
