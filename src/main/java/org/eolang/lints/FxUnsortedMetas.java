/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.List;

/**
 * Fix for the {@code unsorted-metas} lint.
 * Sorts {@code meta} elements inside {@code metas} alphabetically
 * by their {@code head} and {@code tail} content.
 * @since 0.2.1
 */
public final class FxUnsortedMetas implements Fix {

    /**
     * Delegate fix.
     */
    private final Fix delegate;

    /**
     * Ctor.
     * @checkstyle ConstructorsCodeFreeCheck (5 lines)
     */
    public FxUnsortedMetas() {
        this.delegate = new FxByXsl(List.of("org/eolang/fixes/metas/unsorted-metas.xsl"));
    }

    @Override
    public XML apply(final XML xmir) throws IOException {
        return this.delegate.apply(xmir);
    }
}
