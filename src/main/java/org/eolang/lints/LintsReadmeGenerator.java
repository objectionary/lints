/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

/**
 * Generates the lints list for README.
 * Called via `mvn exec:java` on push to master.
 * @since 0.0.1
 */
public final class LintsReadmeGenerator {

    /**
     * Main entry point.
     * @param args Path to README.md
     * @throws IOException If file I/O fails
     */
    public static void main(final String... args) throws IOException {
        if (args.length < 1) {
            System.err.println("Usage: LintsReadmeGenerator <path-to-README.md>");
            System.exit(1);
        }
        final Path readme = Path.of(args[0]);
        final MonoLints all = new MonoLints();
        final String list = all.stream()
            .map(lint -> String.format("  - `%s`", lint.name()))
            .collect(Collectors.joining("\n"));
        final String start = "<!-- LINTS_START -->";
        final String end = "<!-- LINTS_END -->";
        final String content = Files.readString(readme)
            .replaceAll(
                "(?s)(" + java.util.regex.Pattern.quote(start) + ").*(?:" + java.util.regex.Pattern.quote(end) + ")?",
                start + "\n" + list + "\n" + end
            );
        Files.writeString(readme, content);
        System.out.println("README updated with " + all.stream().count() + " lints");
    }
}
