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
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.IoCheckedText;
import org.cactoos.text.TextOf;

/**
 * Fix implemented by a sequence of XSL stylesheets.
 * @since 0.2.1
 */
public final class FxByXsl implements Fix {

    /**
     * XSL stylesheets to apply in order.
     */
    private final List<XSL> sheets;

    /**
     * Constructor.
     * @param paths Classpath resource paths to XSL stylesheets (leading slash optional)
     * @checkstyle ConstructorsCodeFreeCheck (5 lines)
     */
    public FxByXsl(final List<String> paths) {
        this.sheets = FxByXsl.load(paths);
    }

    @Override
    public XML apply(final XML xmir) throws IOException {
        XML result = xmir;
        for (final XSL sheet : this.sheets) {
            result = sheet.transform(result);
        }
        return result;
    }

    private static List<XSL> load(final List<String> paths) {
        final List<XSL> result = new ArrayList<>(paths.size());
        for (final String path : paths) {
            try {
                result.add(
                    new XSLDocument(
                        new IoCheckedText(
                            new TextOf(new ResourceOf(path.replaceFirst("^/", "")))
                        ).asString()
                    ).with(new ClasspathSources())
                );
            } catch (final IOException ex) {
                throw new UncheckedIOException(
                    String.format("Failed to load XSL stylesheet: %s", path),
                    ex
                );
            }
        }
        return result;
    }
}
