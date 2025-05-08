/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.cactoos.func.CheckedFunc;

/**
 * Whole EO program, as collection of XMIR sources to analyze.
 * To get the current version of `lints`, you should read it from
 * MANIFEST.MF file, packaged with library. You can do it like this:
 * <pre>
 * {@code
 * import com.jcabi.manifests.Manifests;
 * final String version = Manifests.read("Lints-Version");
 * }
 * </pre>
 *
 * @see <a href="https://news.eolang.org/2022-11-25-xmir-guide.html">XMIR</a>
 * @since 0.0.49
 */
public class Program {
    /**
     * EO package.
     */
    private final Package pkg;

    /**
     * All sources of EO package {@link #pkg}.
     */
    private final Collection<Source> srcs;

    /**
     * Ctor.
     *
     * @param dirs The directory
     * @throws IOException If fails
     */
    public Program(final Path... dirs) throws IOException {
        this(
            new Package(dirs),
            Program.sources(
                Arrays.asList(dirs),
                new CheckedFunc<>(Source::new, IOException.class::cast)
            )
        );
    }

    /**
     * Ctor.
     *
     * <p>Pay attention, it's important to use {@link Collection} as a type
     * of argument, because {@link Path} implements {@link Iterable}.</p>
     *
     * @param dirs The directory
     * @throws IOException If fails
     */
    public Program(final Collection<Path> dirs) throws IOException {
        this(
            new Package(dirs),
            Program.sources(dirs, new CheckedFunc<>(Source::new, IOException.class::cast))
        );
    }

    /**
     * Ctor.
     *
     * @param map The map with them
     */
    public Program(final Map<String, XML> map) {
        this(
            new Package(map),
            Program.sources(
                map.values(),
                new CheckedFunc<>(Source::new, RuntimeException.class::cast)
            )
        );
    }

    Program(final Package pkg, final Collection<Source> srcs) {
        this.pkg = pkg;
        this.srcs = srcs;
    }

    /**
     * Program with disabled lints.
     *
     * @param names Lint names
     * @return Program analysis without specifics names
     */
    public Program without(final String... names) {
        return new Program(
            this.pkg.without(names),
            this.srcs.stream()
                .map(source -> source.without(names))
                .collect(Collectors.toList())
        );
    }

    /**
     * Find all possible defects in the EO program.
     *
     * @return All defects found
     * @see <a href="https://news.eolang.org/2022-11-25-xmir-guide.html">XMIR guide</a>
     * @see <a href="https://www.eolang.org/XMIR.html">XMIR specification</a>
     * @see <a href="https://www.eolang.org/XMIR.xsd">XMIR schema</a>
     */
    public Collection<Defect> defects() {
        return Stream.concat(
            this.pkg.defects().stream(),
            this.srcs.stream().map(Source::defects).flatMap(Collection::stream)
        ).collect(Collectors.toList());
    }

    private static <T, E extends Exception> Collection<Source> sources(final Collection<T> elements,
        final CheckedFunc<T, Source, E> ctor) throws E {
        final ArrayList<Source> sources = new ArrayList<>(elements.size());
        for (final T element : elements) {
            sources.add(ctor.apply(element));
        }
        return sources;
    }
}
