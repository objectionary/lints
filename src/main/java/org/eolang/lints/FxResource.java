/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.Collections;
import org.cactoos.scalar.Sticky;
import org.cactoos.scalar.Synced;
import org.cactoos.scalar.Unchecked;

/**
 * Fix loaded from a classpath resource.
 * If the resource does not exist, the XMIR is returned unchanged.
 * @since 0.2.1
 */
public final class FxResource implements Fix {

    /**
     * Lazily loaded delegate fix.
     */
    private final Unchecked<Fix> delegate;

    /**
     * Ctor.
     * @param path Classpath path to the XSL fix stylesheet
     */
    public FxResource(final String path) {
        this.delegate = new Unchecked<>(
            new Synced<>(
                new Sticky<>(
                    () -> FxResource.load(path)
                )
            )
        );
    }

    @Override
    public XML apply(final XML xmir) throws IOException {
        return this.delegate.value().apply(xmir);
    }

    /**
     * Load fix from classpath if available, otherwise return no-op.
     * @param path Classpath path to the XSL fix stylesheet
     * @return Fix
     */
    private static Fix load(final String path) {
        final Fix fix;
        if (FxResource.class.getClassLoader().getResource(path) != null) {
            fix = new FxByXsl(Collections.singletonList(path));
        } else {
            fix = new FxEmpty();
        }
        return fix;
    }
}
