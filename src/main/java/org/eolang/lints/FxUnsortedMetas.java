/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.ClasspathSources;
import com.jcabi.xml.XML;
import com.jcabi.xml.XSL;
import com.jcabi.xml.XSLDocument;
import java.io.IOException;
import org.cactoos.io.ResourceOf;
import org.cactoos.scalar.Sticky;
import org.cactoos.scalar.Unchecked;
import org.cactoos.text.IoCheckedText;
import org.cactoos.text.TextOf;

/**
 * Fix for the {@code unsorted-metas} lint.
 * Sorts {@code meta} elements inside {@code metas} alphabetically
 * by their {@code head} and {@code tail} content.
 * @since 0.2.1
 */
public final class FxUnsortedMetas implements Fix {

    /**
     * The XSL stylesheet.
     */
    private final Unchecked<XSL> sheet;

    /**
     * Ctor.
     */
    public FxUnsortedMetas() {
        this.sheet = new Unchecked<>(
            new Sticky<>(
                () -> new XSLDocument(
                    new IoCheckedText(
                        new TextOf(
                            new ResourceOf("org/eolang/fixes/metas/unsorted-metas.xsl")
                        )
                    ).asString()
                ).with(new ClasspathSources())
            )
        );
    }

    @Override
    public XML apply(final XML xmir) throws IOException {
        return this.sheet.value().transform(xmir);
    }
}
