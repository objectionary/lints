/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.cactoos.func.Chained;
import org.cactoos.iterable.Mapped;
import org.cactoos.list.Joined;
import org.cactoos.list.ListOf;

/**
 * Implementation of Project. Provides several ways to make Project object.
 *
 * @since 0.0.9
 */
public final class ProjectOf implements Project {

    /**
     * Collection of programs that should be analyzed independently.
     */
    private final Iterable<Program> programs;

    /**
     * Programs that should be analyzed together.
     */
    private final Programs wpa;

    /**
     * Ctor.
     * @param xml XML source.
     */
    public ProjectOf(final XML xml) {
        this(xml, "single");
    }

    /**
     * Ctor.
     * @param xml XML source.
     * @param name File name.
     */
    public ProjectOf(final XML xml, final String name) {
        this(
            new Program(xml),
            new Programs(Map.of(name, xml))
        );
    }

    /**
     * Ctor.
     * @param home Path to file.
     * @throws IOException Throw IOException in case of problems working with files.
     */
    public ProjectOf(final Path home) throws IOException {
        this(
            new Program(home),
            new Programs(home)
        );
    }

    /**
     * Ctor.
     * @param programs Set of programs XMLs with names.
     * @param list List of lints for independent analyze.
     * @param wpa List of lints for full project analyze.
     */
    public ProjectOf(
        final Map<String, XML> programs,
        final Iterable<Lint<XML>> list,
        final Iterable<Lint<Map<String, XML>>> wpa
    ) {
        this(
            new Mapped<>(
                xml -> new Program(xml, list),
                programs.values()
            ),
            new Programs(programs, wpa)
        );
    }

    /**
     * Ctor.
     * @param program Single program.
     * @param wpa Programs for whole program analysis.
     */
    ProjectOf(final Program program, final Programs wpa) {
        this(
            new ListOf<>(program),
            wpa
        );
    }

    /**
     * Ctor.
     * @param programs List of single programs.
     * @param wpa Programs for whole program analysis.
     */
    ProjectOf(final Iterable<Program> programs, final Programs wpa) {
        this.programs = programs;
        this.wpa = wpa;
    }

    @Override
    public Collection<Defect> singleDefects() {
        return new Joined<>(
            new Mapped<List<Defect>>(
                new Chained<>(Program::defects, ListOf::new),
                this.programs
            )
        );
    }

    @Override
    public Collection<Defect> wpaDefects() {
        return this.wpa.defects();
    }
}
