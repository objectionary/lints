/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.github.lombrozo.xnav.Xnav;
import com.jcabi.xml.XML;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.cactoos.io.InputOf;
import org.cactoos.io.ResourceOf;
import org.cactoos.io.UncheckedInput;
import org.cactoos.list.ListOf;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;
import org.eolang.parser.EoSyntax;

/**
 * Lint for reserved names.
 *
 * @since 0.0.44
 */
final class LtReservedName implements Lint<XML> {

    /**
     * Home objects regex.
     */
    private static final Pattern HOME_OBJECTS = Pattern.compile(".*/downloaded/home/objects");

    /**
     * Non-unix file separators.
     */
    private static final Pattern NON_UNIX = Pattern.compile("\\\\");

    /**
     * Reserved names.
     * The key is object name, the value is the path to EO file.
     */
    private final Map<String, String> reserved;

    /**
     * Ctor.
     */
    LtReservedName() {
        this(LtReservedName.home(Paths.get("downloaded", "home").toString()));
    }

    /**
     * Ctor.
     *
     * @param names Reserved names
     */
    LtReservedName(final Map<String, String> names) {
        this.reserved = names;
    }

    @Override
    public String name() {
        return "reserved-name";
    }

    @Override
    public Collection<Defect> defects(final XML xmir) throws IOException {
        final Collection<Defect> defects = new LinkedList<>();
        final Xnav program = new Xnav(xmir.inner());
        program.path("//o[@name]")
            .forEach(
                object -> {
                    final String oname = object.attribute("name").text().get();
                    if (this.reserved.keySet().contains(oname)) {
                        defects.add(
                            new Defect.Default(
                                this.name(),
                                Severity.WARNING,
                                program.element("program").attribute("name")
                                    .text().orElse("unknown"),
                                Integer.parseInt(object.attribute("line").text().orElse("0")),
                                String.format(
                                    "Object name \"%s\" is already reserved by object in the \"%s\"",
                                    oname, this.reserved.get(oname)
                                )
                            )
                        );
                    }
                }
            );
        return defects;
    }

    @Override
    public String motive() throws IOException {
        return new UncheckedText(
            new TextOf(
                new ResourceOf(
                    String.format(
                        "org/eolang/motives/names/%s.md", this.name()
                    )
                )
            )
        ).asString();
    }

    /**
     * Locate reserved names from home EO objects.
     * During the `generate-sources` maven phase we are downloading <a href="https://github.com/objectionary/home">home repo</a>
     * as the source of object names. After repo downloaded, during `process-resources` phase,
     * we copy downloaded repo to classes in lints JAR: `${project.build.directory}/classes/`.
     * This is mandatory step in order to provide access to the home repo files, when the programs
     * are linted from the outside the lints project, using lints as dependency. While, in local
     * tests, home repo is accessed as normal file.
     * Both methods depend on the same directory, which we pass in the ctor, the only difference
     * in the term of access - for JAR we need to "mount" the file system using {@link FileSystem}.
     * @param location Location of home repo.
     * @return Map of reserved names
     */
    private static Map<String, String> home(final String location) {
        final List<Map<String, String>> names = new ListOf<>();
        final URL resource = Thread.currentThread().getContextClassLoader().getResource(location);
        final Predicate<Path> sources = p -> {
            final String file = p.toString().replace("\\", "/");
            return file.endsWith(".eo")
                && file.contains(
                    Path.of(location)
                        .resolve("objects")
                        .resolve("org")
                        .resolve("eolang")
                        .toString().replace("\\", "/")
            );
        };
        if ("jar".equals(resource.getProtocol())) {
            final URI uri = URI.create(
                String.format(
                    "jar:file:%s",
                    resource.getFile().substring(5, resource.getFile().indexOf('!'))
                )
            );
            try (FileSystem mount = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
                Files.walk(mount.getPath(location))
                    .filter(sources)
                    .forEach(eo -> names.add(LtReservedName.namesInJar(eo)));
            } catch (final IOException exception) {
                throw new IllegalStateException(
                    "Failed to read home objects from JAR", exception
                );
            }
        } else {
            try {
                Files.walk(Paths.get(resource.toURI()))
                    .filter(sources)
                    .forEach(eo -> names.add(LtReservedName.namesInFile(eo)));
            } catch (final IOException exception) {
                throw new IllegalStateException("Failed to walk through files", exception);
            } catch (final URISyntaxException exception) {
                throw new IllegalStateException("URI syntax is broken", exception);
            }
        }
        return names.stream()
            .flatMap(map -> map.entrySet().stream())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Names in EO file.
     * @param path Path to EO file
     * @return Map of names, the key is object name, the value us path
     */
    private static Map<String, String> namesInFile(final Path path) {
        final XML parsed;
        try {
            parsed = new EoSyntax("reserved", new UncheckedInput(new InputOf(path.toFile())))
                .parsed();
        } catch (final IOException exception) {
            throw new IllegalStateException(
                String.format("Failed to parse EO source in \"%s\"", path),
                exception
            );
        }
        return LtReservedName.namesInXmir(parsed, path);
    }

    /**
     * Names in EO file from JAR.
     * @param path Path to EO file in JAR
     * @return Map of names, the key is object name, the value us path
     * @checkstyle IllegalCatchCheck (15 lines)
     */
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    private static Map<String, String> namesInJar(final Path path) {
        final XML parsed;
        try (InputStream input = Files.newInputStream(path)) {
            parsed = new EoSyntax("reserved", new TextOf(input).asString()).parsed();
        } catch (final Exception exception) {
            throw new IllegalStateException(
                String.format("Failed to parse EO source in \"%s\"", path),
                exception
            );
        }
        return LtReservedName.namesInXmir(parsed, path);
    }

    /**
     * High-level object names in XMIR.
     * @param xmir XMIR
     * @param path EO source file path
     * @return Map of object names in XMIR
     */
    private static Map<String, String> namesInXmir(final XML xmir, final Path path) {
        final Map<String, String> names = new HashMap<>(64);
        new Xnav(xmir.inner()).path("/program/objects/o/@name")
            .map(oname -> oname.text().get())
            .forEach(
                oname ->
                    names.put(
                        oname,
                        LtReservedName.HOME_OBJECTS.matcher(
                            LtReservedName.NON_UNIX.matcher(path.toString()).replaceAll("/")
                            )
                            .replaceFirst("")
                            .substring(1)
                            .replace("/", ".")
                            .replace("\"", ".")
                    )
            );
        return names;
    }
}
