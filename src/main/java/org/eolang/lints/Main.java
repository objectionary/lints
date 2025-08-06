/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Stream;

/**
 * CLI entry point for lints analyzer.
 *
 * @since 0.2.0
 */
@SuppressWarnings("PMD.UseUtilityClass")
final class Main {
    /**
     * Utility class constructor.
     */
    private Main() {
        // Constructor is private to prevent instantiation
    }

    /**
     * Entry point.
     * @param args Command line arguments
     * @throws IOException If fails to read files
     */
    @SuppressWarnings("PMD.SystemPrintln")
    public static void main(final String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println("Usage: java -jar lints.jar <path-to-xmir-file>");
            System.exit(1);
        }
        final Path path = FileSystems.getDefault().getPath(args[0]);
        if (Files.isDirectory(path)) {
            processDirectory(path);
        } else {
            processFile(path);
        }
    }

    /**
     * Process single XMIR file.
     * @param file Path to XMIR file
     * @throws IOException On read error
     */
    @SuppressWarnings({"PMD.SystemPrintln", "PMD.SystemPrintf"})
    private static void processFile(final Path file) throws IOException {
        System.out.printf("Analyzing %s:%n", file);
        final Collection<Defect> defects = new Source(file).defects();
        if (defects.isEmpty()) {
            System.out.println("✔ No defects found");
        } else {
            defects.forEach(d -> System.out.printf("✖ %s%n", d));
        }
    }

    /**
     * Process directory with XMIR files.
     * @param dir Path to directory
     * @throws IOException On read error
     */
    @SuppressWarnings({"PMD.SystemPrintln", "PMD.SystemPrintf"})
    private static void processDirectory(final Path dir) throws IOException {
        try (Stream<Path> stream = Files.walk(dir)) {
            stream
                .filter(
                    p -> p.toString().endsWith(".xmir")
                )
                .forEach(
                    p -> {
                        try {
                            Main.processFile(p);
                        } catch (final IOException ex) {
                            System.err.printf(
                                "Failed to process %s: %s%n",
                                p,
                                ex.getMessage()
                            );
                        }
                    }
                );
        }
    }
}
