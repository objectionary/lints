/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package fixtures;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

/**
 * A single YAML test pack file on disk, exposing its parsed content
 * and whether its use of raw XMIR (the {@code document} pack key)
 * is justified by an accompanying {@code xml-reason} key.
 *
 * @since 0.2.1
 */
public final class YamlPack {

    /**
     * Path to the pack file.
     */
    private final Path source;

    /**
     * Parsed YAML content.
     */
    private final Map<String, Object> pack;

    /**
     * Ctor.
     *
     * @param path Path to the YAML pack file
     */
    public YamlPack(final Path path) {
        this(path, YamlPack.load(path));
    }

    /**
     * Ctor.
     *
     * @param path Path to the YAML pack file
     * @param pack Parsed YAML content
     */
    private YamlPack(final Path path, final Map<String, Object> pack) {
        this.source = path;
        this.pack = pack;
    }

    /**
     * Path to this pack file.
     *
     * @return The path
     */
    public Path path() {
        return this.source;
    }

    /**
     * Does this pack use a raw XMIR document instead of EO source?
     *
     * @return True if the pack has a {@code document} key
     */
    public boolean hasDocument() {
        return this.pack.containsKey("document");
    }

    /**
     * Is the use of {@code document} (if any) justified by an
     * {@code xml-reason} key explaining why raw XMIR was required?
     *
     * @return True if the pack has no {@code document}, or has one
     *  together with an {@code xml-reason}
     */
    public boolean documentUsageJustified() {
        return !this.hasDocument() || this.pack.containsKey("xml-reason");
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> load(final Path path) {
        try {
            return (Map<String, Object>) new Yaml().load(
                new String(Files.readAllBytes(path), StandardCharsets.UTF_8)
            );
        } catch (final IOException ex) {
            throw new IllegalStateException(
                String.format("Failed to read/parse YAML pack '%s'", path),
                ex
            );
        }
    }
}
