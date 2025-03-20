/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import org.cactoos.iterable.Filtered;
import org.cactoos.iterable.Sticky;
import org.cactoos.iterable.Synced;
import org.cactoos.list.ListOf;

/**
 * A single XMIR program to analyze.
 *
 * @see <a href="https://news.eolang.org/2022-11-25-xmir-guide.html">XMIR</a>
 * @since 0.1.0
 */
final class Program {

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
    Program(final Path file) throws FileNotFoundException {
        this(new XMLDocument(file));
    }

    /**
     * Ctor.
     * @param xml The XMIR
     */
    Program(final XML xml) {
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
     * Program with disabled lints.
     * @param names Lint names
     * @return Program analysis without specific name
     */
    public Program without(final String... names) {
        final Collection<String> listed = new ListOf<>(names);
        return new Program(
            this.xmir,
            new Filtered<>(
                this.lints, lint -> () -> !listed.contains(lint.name())
            )
        );
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
