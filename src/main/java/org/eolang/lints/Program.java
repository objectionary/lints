/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2025 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import org.cactoos.iterable.Sticky;
import org.cactoos.iterable.Synced;

/**
 * A single XMIR program to analyze.
 *
 * @see <a href="https://news.eolang.org/2022-11-25-xmir-guide.html">XMIR</a>
 * @since 0.1.0
 */
public final class Program {

    /**
     * Collection of mono lints, preloaded on JVM start.
     */
    private static final Iterable<Lint<XML>> MONO = new Synced<>(
        new Sticky<>(
            new PkMono()
        )
    );

    /**
     * The XMIR program to analyze.
     */
    private final XML xmir;

    /**
     * Lint to use.
     */
    private final Iterable<Lint<XML>> lints;

    /**
     * Ctor.
     * @param file The absolute path of the XMIR file
     * @throws FileNotFoundException If file isn't found
     */
    public Program(final Path file) throws FileNotFoundException {
        this(new XMLDocument(file));
    }

    /**
     * Ctor.
     * @param xml The XMIR
     */
    public Program(final XML xml) {
        this(xml, Program.MONO);
    }

    /**
     * Ctor.
     *
     * <p>This constructor is for internal use only. It is not supposed
     * to be visible by end-users. Keep it this way!</p>
     *
     * @param xml The XMIR
     * @param list The lints
     */
    Program(final XML xml, final Iterable<Lint<XML>> list) {
        this.xmir = xml;
        this.lints = list;
    }

    /**
     * Find defects possible defects in the XMIR file.
     * @return All defects found
     * @see <a href="https://news.eolang.org/2022-11-25-xmir-guide.html">XMIR guide</a>
     * @see <a href="https://www.eolang.org/XMIR.html">XMIR specification</a>
     * @see <a href="https://www.eolang.org/XMIR.xsd">XMIR schema</a>
     */
    public Collection<Defect> defects() {
        try {
            final Collection<Defect> messages = new ArrayList<>(0);
            for (final Lint<XML> lint : this.lints) {
                messages.addAll(lint.defects(this.xmir));
            }
            return messages;
        } catch (final IOException ex) {
            throw new IllegalStateException(
                "Failed to find defects in the XMIR file",
                ex
            );
        }
    }
}
