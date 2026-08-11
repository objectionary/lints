/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.cactoos.iterable.Sticky;
import org.cactoos.iterable.Synced;

/**
 * A single source XMIR to analyze.
 * @see <a href="https://news.eolang.org/2022-11-25-xmir-guide.html">XMIR</a>
 * @since 0.1.0
 */
public final class Source {

    /**
     * Collection of mono lints, preloaded on JVM start.
     */
    private static final Iterable<Lint> MONO = new Synced<>(
        new Sticky<>(
            new PkMono()
        )
    );

    /**
     * The XMIR source to analyze.
     */
    private final XML xmir;

    /**
     * Lint to use.
     */
    private final Iterable<Lint> lints;

    /**
     * Disabled lint names.
     */
    private final Collection<String> disabled;

    /**
     * Ctor.
     * @param file The absolute path of the XMIR file
     * @throws FileNotFoundException If file isn't found
     */
    public Source(final Path file) throws FileNotFoundException {
        this(new XMLDocument(file));
    }

    /**
     * Ctor.
     * @param xml The XMIR
     */
    public Source(final XML xml) {
        this(xml, Source.MONO);
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
    Source(final XML xml, final Iterable<Lint> list) {
        this(xml, list, new ArrayList<>(0));
    }

    /**
     * Ctor.
     * @param xml The XMIR
     * @param list The lints
     * @param names Disabled lint names
     */
    private Source(final XML xml, final Iterable<Lint> list, final Collection<String> names) {
        this.xmir = xml;
        this.lints = list;
        this.disabled = names;
    }

    /**
     * Source with disabled lints.
     * @param first Lint name to disable
     * @param rest Lint names to disable
     * @return Program analysis without specific names
     */
    public Source without(final String first, final String... rest) {
        final Collection<String> names = new ArrayList<>(this.disabled);
        names.add(first);
        names.addAll(Arrays.asList(rest));
        final Set<String> known = new HashSet<>(0);
        for (final Lint lint : new PkMono()) {
            known.add(lint.name());
        }
        for (final String name : names) {
            if (!known.contains(name)) {
                throw new IllegalArgumentException(
                    String.format("Unknown lint name: %s", name)
                );
            }
        }
        return new org.eolang.lints.Source(
            this.xmir,
            new MonoWithout(names.toArray(new String[0])),
            names
        );
    }

    /**
     * Apply all available fixes to the XMIR, returning the corrected document.
     * Lints without a fix return the XMIR unchanged.
     * @return Fixed XMIR
     * @throws IOException If a fix fails to apply
     */
    public XML fix() throws IOException {
        XML result = this.xmir;
        for (final Lint lint : this.lints) {
            result = lint.fix().apply(result);
        }
        return result;
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
            for (final Lint lint : this.lints) {
                messages.addAll(new ScopedDefects(lint.defects(this.xmir), "S"));
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
