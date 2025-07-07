/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints

import com.github.lombrozo.xnav.Xnav
import com.jcabi.xml.XML
import com.yegor256.tojos.MnCsv
import com.yegor256.tojos.TjCached
import com.yegor256.tojos.TjDefault
import com.yegor256.tojos.TjSynchronized
import com.yegor256.tojos.Tojos
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.function.Predicate
import java.util.regex.Pattern
import java.util.stream.Stream
import org.cactoos.io.InputOf
import org.cactoos.io.UncheckedInput
import org.cactoos.list.ListOf
import org.cactoos.text.TextOf
import org.eolang.parser.EoSyntax

/**
 * Home object names in the CSV file.
 * @since 0.0.49
 * @todo #465:45min Refactor HomeNames.
 *  Currently, the class does two things: 1) Object name parsing, and 2) CSV placing.
 *  Let's refactor it to make it aligned with <a href="https://en.wikipedia.org/wiki/Single-responsibility_principle">SRP</a>.
 *  Probably one new class will be required (for name parsing), together with proper
 *  naming of this one (for placing them into CSV).
 */
final class HomeNames {

/**
 * Home objects regex.
 */
  private static final Pattern HOME_OBJECTS = Pattern.compile(".*/downloaded/home/objects")

  /**
   * Non-unix file separators.
   */
  private static final Pattern NON_UNIX = Pattern.compile("\\\\")

  /**
   * Reserved store.
   */
  private final Tojos placed

  /**
   * Home objects location
   */
  private final String location

  /**
   * Ctor.
   * @param home Home objects location
   */
  HomeNames(final Path home) {
    this(home.toString())
  }

  /**
   * Ctor.
   * @param Home objects location
   */
  HomeNames(final String hloc) {
    this("target/classes/reserved.csv", hloc)
  }

  /**
   * Ctor.
   * @param csv CSV file path
   * @param hloc Home objects location
   */
  HomeNames(final String csv, final String hloc) {
    this(
      new TjCached(
        new TjSynchronized(
          new TjDefault(
            new MnCsv(csv)
          )
        )
      ),
      hloc
    )
  }

  /**
   * Ctor.
   * @param tjs Reserved store
   * @param hloc Home location
   */
  HomeNames(final Tojos tjs, final String hloc) {
    this.placed = tjs
    this.location = hloc;
  }

  /**
   * Place them into CSV file.
   * Here, we are receiving names from either JAR or normal file depending, where the source of home
   * objects is located. When `lints` used as a dependency, home repo is accessed from JAR, while,
   * in local tests, we use read as normal file on disk.
   * Both methods: {@link HomeNames#namesInJar} and {@link HomeNames#namesInFile} depend on
   * the same directory, which we pass in the ctor, the only difference in the term of access - for
   * JAR we need to "mount" the file system using {@link FileSystem}.
   */
  void placeCsv() {
    final List<Map<String, String>> names = new ListOf<>()
    final URL resource = Thread.currentThread().contextClassLoader.getResource(this.location)
    final Predicate<Path> sources = p -> {
      final String file = p.toString().replace("\\", "/")
      return file.endsWith(".eo")
        && file.contains(
        Path.of(this.location)
          .resolve("objects")
          .resolve("org")
          .resolve("eolang")
          .toString().replace("\\", "/")
      )
    }
    if ("jar" == resource.protocol) {
      final URI uri = URI.create(
        "jar:file:${resource.file.substring(5, resource.file.indexOf('!'))}",
      )
      try (
        FileSystem mount = FileSystems.newFileSystem(uri, Collections.emptyMap())
        Stream<Path> paths = Files.walk(mount.getPath(this.location))
      ) {
        paths.filter(sources)
          .forEach(eo -> names.add(namesInJar(eo)))
      } catch (final IOException exception) {
        throw new IllegalStateException(
          "Failed to read home objects from JAR", exception
        )
      }
    } else {
      try (Stream<Path> paths = Files.walk(Paths.get(resource.toURI()))) {
        paths.filter(sources)
          .forEach(eo -> names.add(namesInFile(eo)))
      } catch (final IOException exception) {
        throw new IllegalStateException("Failed to walk through files", exception)
      } catch (final URISyntaxException exception) {
        throw new IllegalStateException("URI syntax is broken", exception)
      }
    }
    names.stream()
      .flatMap(map -> map.entrySet().stream())
      .forEach(
        entry -> this.placed.add(entry.key).set("path", entry.value)
      )
  }

  /**
   * Names in EO file.
   * @param path Path to EO file
   * @return Map of names, the key is object name, the value us path
   */
  private static Map<String, String> namesInFile(final Path path) {
    final XML parsed
    try {
      parsed = new EoSyntax(new UncheckedInput(new InputOf(path.toFile())))
        .parsed()
    } catch (final IOException exception) {
      throw new IllegalStateException("Failed to parse EO source in \"${path}\"", exception)
    }
    return namesInXmir(parsed, path)
  }

  /**
   * Names in EO file from JAR.
   * @param path Path to EO file in JAR
   * @return Map of names, the key is object name, the value us path
   * @checkstyle IllegalCatchCheck (15 lines)
   */
  @SuppressWarnings("PMD.AvoidCatchingGenericException")
  private static Map<String, String> namesInJar(final Path path) {
    final XML parsed
    try (InputStream input = Files.newInputStream(path)) {
      parsed = new EoSyntax(new TextOf(input).asString()).parsed()
    } catch (final Exception exception) {
      throw new IllegalStateException("Failed to parse EO source in \"${path}\"", exception)
    }
    return namesInXmir(parsed, path)
  }

  /**
   * High-level object names in XMIR.
   * @param xmir XMIR
   * @param path EO source file path
   * @return Map of object names in XMIR
   */
  private static Map<String, String> namesInXmir(final XML xmir, final Path path) {
    final Map<String, String> names = new HashMap<>(64)
    new Xnav(xmir.inner()).path("/object/o/@name")
      .map(oname -> oname.text().get())
      .forEach(
        oname ->
          names[oname] = HOME_OBJECTS.matcher(
            NON_UNIX.matcher(path.toString()).replaceAll("/")
          )
            .replaceFirst("")
            .substring(1)
            .replace("/", ".")
            .replace("\"", ".")
      )
    return names
  }
}
