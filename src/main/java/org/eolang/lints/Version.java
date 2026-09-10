/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A strict {@code major.minor.patch} version.
 *
 * @since 0.2.11
 */
final class Version {

    /**
     * Matches the numeric {@code major.minor.patch} core of a version string.
     */
    private static final Pattern CORE = Pattern.compile("^(\\d+)\\.(\\d+)\\.(\\d+)");

    /**
     * Major version.
     */
    private final int major;

    /**
     * Minor version.
     */
    private final int minor;

    /**
     * Patch version.
     */
    private final int patch;

    /**
     * Ctor.
     *
     * @param mjr Major version
     * @param mnr Minor version
     * @param pch Patch version
     */
    Version(final int mjr, final int mnr, final int pch) {
        this.major = mjr;
        this.minor = mnr;
        this.patch = pch;
    }

    @Override
    public String toString() {
        return String.format("%d.%d.%d", this.major, this.minor, this.patch);
    }

    static Optional<Version> parsed(final String text) {
        final Matcher matcher = Version.CORE.matcher(text.trim());
        final Optional<Version> result;
        if (matcher.find()) {
            result = Optional.of(
                new Version(
                    Integer.parseInt(matcher.group(1)),
                    Integer.parseInt(matcher.group(2)),
                    Integer.parseInt(matcher.group(3))
                )
            );
        } else {
            result = Optional.empty();
        }
        return result;
    }

    boolean newerThan(final Version other) {
        final boolean newer;
        if (this.major == other.major) {
            if (this.minor == other.minor) {
                newer = this.patch > other.patch;
            } else {
                newer = this.minor > other.minor;
            }
        } else {
            newer = this.major > other.major;
        }
        return newer;
    }
}
