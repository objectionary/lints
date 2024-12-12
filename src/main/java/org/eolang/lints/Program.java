/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2024 Objectionary.com
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
import java.util.Collection;
import java.util.LinkedList;

/**
 * A single XMIR program to analyze.
 *
 * @see <a href="https://news.eolang.org/2022-11-25-xmir-guide.html">XMIR</a>
 * @since 0.1.0
 */
public final class Program {

    /**
     * Lints to use.
     */
    public static final Iterable<Lint<XML>> LINTS = new ProgramLints().value();

    /**
     * The XMIR program to analyze.
     */
    private final XML xmir;

    /**
     * Ctor.
     * @param xml The XMIR
     */
    public Program(final XML xml) {
        this.xmir = xml;
    }

    /**
     * Ctor.
     * @param file The absolute path of the XMIR file
     * @throws FileNotFoundException If file not found
     */
    public Program(final Path file) throws FileNotFoundException {
        this(new XMLDocument(file));
    }

    /**
     * Find defects possible defects in the XMIR file.
     * @return All defects found
     */
    public Collection<Defect> defects() throws IOException {
        final Collection<Defect> messages = new LinkedList<>();
        for (final Lint<XML> lint : Program.LINTS) {
            messages.addAll(lint.defects(this.xmir));
        }
        return messages;
    }

}
