/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.github.lombrozo.xnav.Xnav;
import com.jcabi.xml.XML;
import com.yegor256.tojos.MnCsv;
import com.yegor256.tojos.TjCached;
import com.yegor256.tojos.TjDefault;
import com.yegor256.tojos.TjSynchronized;
import com.yegor256.tojos.Tojo;
import com.yegor256.tojos.Tojos;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.cactoos.Proc;
import org.cactoos.io.InputOf;
import org.cactoos.io.UncheckedInput;
import org.cactoos.list.ListOf;
import org.cactoos.text.TextOf;
import org.eolang.parser.EoSyntax;

/**
 * Reserved object names by EO home objects.
 * Locate reserved names from home EO objects.
 * During the `generate-sources` maven phase we are downloading <a href="https://github.com/objectionary/home">home repo</a>
 * as the source of object names. After repo downloaded, during `process-resources` phase,
 * we copy downloaded repo to classes in lints JAR: `${project.build.directory}/classes/`.
 * This is mandatory step in order to provide access to the home repo files, when the sources
 * are linted from the outside the lints project, using lints as dependency. While, in local
 * tests, home repo is accessed as normal file.
 * Both methods depend on the same directory, which we pass in the ctor, the only difference
 * in the term of access - for JAR we need to "mount" the file system using {@link FileSystem}.
 * @since 0.0.49
 */
final class ReserveHome implements Proc<String> {
    /**
     * Home objects regex.
     */
    private static final Pattern HOME_OBJECTS = Pattern.compile(".*/downloaded/home/objects");

    /**
     * Non-unix file separators.
     */
    private static final Pattern NON_UNIX = Pattern.compile("\\\\");

    /**
     * Reserved store.
     */
    private final Tojos placed;

    /**
     * Ctor.
     * @param path CSV file path
     */
    ReserveHome(final String path) {
        this(
            new TjCached(
                new TjSynchronized(
                    new TjDefault(
                        new MnCsv(path)
                    )
                )
            )
        );
    }

    /**
     * Ctor.
     * @param tjs Reserved store
     */
    ReserveHome(final Tojos tjs) {
        this.placed = tjs;
    }

    @Override
    public void exec(final String location) {
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
            try (
                final FileSystem mount = FileSystems.newFileSystem(uri, Collections.emptyMap());
                final Stream<Path> paths = Files.walk(mount.getPath(location))
            ) {
                paths.filter(sources)
                    .forEach(eo -> names.add(ReserveHome.namesInJar(eo)));
            } catch (final IOException exception) {
                throw new IllegalStateException(
                    "Failed to read home objects from JAR", exception
                );
            }
        } else {
            try (final Stream<Path> paths = Files.walk(Paths.get(resource.toURI()))) {
                paths.filter(sources)
                    .forEach(eo -> names.add(ReserveHome.namesInFile(eo)));
            } catch (final IOException exception) {
                throw new IllegalStateException("Failed to walk through files", exception);
            } catch (final URISyntaxException exception) {
                throw new IllegalStateException("URI syntax is broken", exception);
            }
        }
        // write them into CSV format under target/classes/reserved.csv
        // invoke this class via Groovy
        // reuse the `reserved.csv`
        names.stream()
            .flatMap(map -> map.entrySet().stream())
            .forEach(entry -> this.placed.add(entry.getKey()).set("path", entry.getValue()));
    }

    /**
     * Names in EO file.
     * @param path Path to EO file
     * @return Map of names, the key is object name, the value us path
     */
    private static Map<String, String> namesInFile(final Path path) {
        final XML parsed;
        try {
            parsed = new EoSyntax(new UncheckedInput(new InputOf(path.toFile())))
                .parsed();
        } catch (final IOException exception) {
            throw new IllegalStateException(
                String.format("Failed to parse EO source in \"%s\"", path),
                exception
            );
        }
        return ReserveHome.namesInXmir(parsed, path);
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
        try (final InputStream input = Files.newInputStream(path)) {
            parsed = new EoSyntax(new TextOf(input).asString()).parsed();
        } catch (final Exception exception) {
            throw new IllegalStateException(
                String.format("Failed to parse EO source in \"%s\"", path),
                exception
            );
        }
        return ReserveHome.namesInXmir(parsed, path);
    }

    /**
     * High-level object names in XMIR.
     * @param xmir XMIR
     * @param path EO source file path
     * @return Map of object names in XMIR
     */
    private static Map<String, String> namesInXmir(final XML xmir, final Path path) {
        final Map<String, String> names = new HashMap<>(64);
        new Xnav(xmir.inner()).path("/object/o/@name")
            .map(oname -> oname.text().get())
            .forEach(
                oname ->
                    names.put(
                        oname,
                        ReserveHome.HOME_OBJECTS.matcher(
                                ReserveHome.NON_UNIX.matcher(path.toString()).replaceAll("/")
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
