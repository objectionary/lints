/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.cactoos.iterable.Sticky;
import org.cactoos.list.ListOf;
import org.cactoos.list.Synced;

/**
 * Whole EO package, as collection of XMIR sources to analyze.
 * @since 0.1.0
 */
final class EoPackage {

    /**
     * Collection of wpa lints, preloaded on JVM start.
     */
    private static final Iterable<Lint<Map<String, XML>>> WPA = new Synced<>(
        new ListOf<>(
            new Sticky<>(
                new PkWpa()
            )
        )
    );

    /**
     * Lints to use.
     */
    private final Iterable<Lint<Map<String, XML>>> lints;

    /**
     * The package of XMIR files.
     */
    private final Map<String, XML> pkg;

    /**
     * Ctor.
     * @param dirs The directory
     * @throws IOException If fails
     */
    EoPackage(final Path... dirs) throws IOException {
        this(Arrays.asList(dirs));
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
    EoPackage(final Collection<Path> dirs) throws IOException {
        this(EoPackage.discover(dirs));
    }

    /**
     * Ctor.
     * @param map The map with them
     */
    EoPackage(final Map<String, XML> map) {
        this(map, EoPackage.WPA);
    }

    /**
     * Ctor.
     * @param map The map with them
     * @param list The lints
     */
    EoPackage(final Map<String, XML> map, final Iterable<Lint<Map<String, XML>>> list) {
        this.pkg = Collections.unmodifiableMap(map);
        this.lints = list;
    }

    /**
     * Package with disabled lints.
     * @param names Lint names
     * @return Package analysis without specifics names
     */
    public EoPackage without(final String... names) {
        return new EoPackage(this.pkg, new WpaWithout(names));
    }

    /**
     * Find all possible defects in the EO package.
     * @return All defects found
     */
    public Collection<Defect> defects() {
        final Collection<Defect> messages = new ArrayList<>(0);
        for (final Lint<Map<String, XML>> lint : this.lints) {
            try {
                messages.addAll(new ScopedDefects(lint.defects(this.pkg), "WPA"));
            } catch (final IOException exception) {
                throw new IllegalStateException(
                    String.format(
                        "Failed to find defects in the '%s' package with '%s' lint",
                        this.pkg,
                        lint
                    ),
                    exception
                );
            }
        }
        return messages;
    }

    /**
     * Discover all XMIR files in the directory.
     * @param dirs The directories to search for XMIR files in (recursively)
     * @return Map of XMIR files
     * @throws IOException If fails
     */
    private static Map<String, XML> discover(final Iterable<Path> dirs) throws IOException {
        final Map<String, XML> map = new HashMap<>(0);
        for (final Path dir : dirs) {
            map.putAll(EoPackage.discover(dir));
        }
        return map;
    }

    /**
     * Discover all XMIR files in the directory.
     * @param dir The directories to search for XMIR files in (recursively)
     * @return Map of XMIR files
     * @throws IOException If fails
     */
    private static Map<String, XML> discover(final Path dir) throws IOException {
        try (Stream<Path> walk = Files.walk(dir)) {
            return walk
                .filter(Files::isRegularFile)
                .collect(
                    Collectors.toMap(
                        path -> new XmirKey(path, dir).asString(),
                        path -> {
                            try {
                                return new XMLDocument(path);
                            } catch (final FileNotFoundException ex) {
                                throw new IllegalArgumentException(ex);
                            }
                        }
                    )
                );
        }
    }

}
