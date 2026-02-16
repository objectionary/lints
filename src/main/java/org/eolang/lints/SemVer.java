/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Semantic Versioning 2.0.0.
 * <p>
 * Parses, validates, and compares version strings that follow the
 * <a href="https://semver.org/">SemVer 2.0.0</a> specification.
 * A valid SemVer string has the format {@code MAJOR.MINOR.PATCH},
 * optionally followed by a hyphen and pre-release identifiers, and/or
 * a plus sign and build metadata.
 * </p>
 *
 * @see <a href="https://semver.org/">Semantic Versioning 2.0.0</a>
 * @since 1.0
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class SemVer implements Comparable<SemVer> {

    /**
     * Full SemVer regex pattern with named groups.
     */
    private static final Pattern PATTERN = Pattern.compile(
        "^(?<major>0|[1-9]\\d*)\\.(?<minor>0|[1-9]\\d*)\\.(?<patch>0|[1-9]\\d*)(?:-(?<prerelease>(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\\+(?<buildmetadata>[0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?$"
    );

    /**
     * Major version number.
     */
    private final int mjr;

    /**
     * Minor version number.
     */
    private final int mnr;

    /**
     * Patch version number.
     */
    private final int ptch;

    /**
     * Pre-release identifiers.
     */
    private final String pre;

    /**
     * Build metadata.
     */
    private final String build;

    /**
     * Ctor.
     * <p>
     * Parses a version string in the format {@code MAJOR.MINOR.PATCH}
     * with optional pre-release and build metadata components.
     * </p>
     *
     * @param version Version string to parse
     * @throws IllegalArgumentException If the string is not valid SemVer
     */
    public SemVer(final String version) {
        this(SemVer.parsed(version));
    }

    /**
     * Ctor.
     * @param major Major version
     * @param minor Minor version
     * @param patch Patch version
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public SemVer(final int major, final int minor, final int patch) {
        this(major, minor, patch, "", "");
    }

    /**
     * Ctor.
     * @param major Major version
     * @param minor Minor version
     * @param patch Patch version
     * @param prerelease Pre-release identifiers
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public SemVer(
        final int major, final int minor, final int patch,
        final String prerelease
    ) {
        this(major, minor, patch, prerelease, "");
    }

    /**
     * Ctor.
     * @param major Major version
     * @param minor Minor version
     * @param patch Patch version
     * @param prerelease Pre-release identifiers (may be null, normalized to empty string)
     * @param meta Build metadata (may be null, normalized to empty string)
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public SemVer(
        final int major, final int minor, final int patch,
        final String prerelease, final String meta
    ) {
        if (major < 0) {
            throw new IllegalArgumentException("Major version must be non-negative");
        }
        if (minor < 0) {
            throw new IllegalArgumentException("Minor version must be non-negative");
        }
        if (patch < 0) {
            throw new IllegalArgumentException("Patch version must be non-negative");
        }
        this.mjr = major;
        this.mnr = minor;
        this.ptch = patch;
        this.pre = prerelease == null ? "" : prerelease;
        this.build = meta == null ? "" : meta;
    }

    /**
     * Private delegating ctor from a parsed SemVer.
     * @param origin Parsed instance
     */
    private SemVer(final SemVer origin) {
        this(origin.mjr, origin.mnr, origin.ptch, origin.pre, origin.build);
    }

    /**
     * Major version number.
     * @return Major version
     */
    public int major() {
        return this.mjr;
    }

    /**
     * Minor version number.
     * @return Minor version
     */
    public int minor() {
        return this.mnr;
    }

    /**
     * Patch version number.
     * @return Patch version
     */
    public int patch() {
        return this.ptch;
    }

    /**
     * Pre-release identifiers.
     * @return Pre-release string, empty if absent
     */
    public String prerelease() {
        return this.pre;
    }

    /**
     * Build metadata.
     * @return Build metadata string, empty if absent
     */
    public String metadata() {
        return this.build;
    }

    /**
     * Whether this version has a pre-release component.
     * @return True if pre-release identifiers are present
     */
    public boolean isPrerelease() {
        return !this.pre.isEmpty();
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder()
            .append(this.mjr)
            .append('.')
            .append(this.mnr)
            .append('.')
            .append(this.ptch);
        if (!this.pre.isEmpty()) {
            result.append('-').append(this.pre);
        }
        if (!this.build.isEmpty()) {
            result.append('+').append(this.build);
        }
        return result.toString();
    }

    @Override
    @SuppressWarnings("PMD.CognitiveComplexity")
    public int compareTo(final SemVer other) {
        int result = Integer.compare(this.mjr, other.mjr);
        if (result == 0) {
            result = Integer.compare(this.mnr, other.mnr);
        }
        if (result == 0) {
            result = Integer.compare(this.ptch, other.ptch);
        }
        if (result == 0) {
            result = SemVer.comparePre(this.pre, other.pre);
        }
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        final boolean result;
        if (this == obj) {
            result = true;
        } else if (obj instanceof SemVer) {
            result = this.compareTo((SemVer) obj) == 0;
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.mjr, this.mnr, this.ptch, this.pre);
    }

    /**
     * Parse version string into a SemVer instance.
     * @param version Version string
     * @return Parsed SemVer
     * @throws IllegalArgumentException If the string is not valid SemVer
     */
    private static SemVer parsed(final String version) {
        final Matcher matcher = SemVer.PATTERN.matcher(version);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(
                String.format("Invalid SemVer: '%s'", version)
            );
        }
        final String prerelease = matcher.group("prerelease");
        final String meta = matcher.group("buildmetadata");
        final String pre;
        if (prerelease == null) {
            pre = "";
        } else {
            pre = prerelease;
        }
        final String bld;
        if (meta == null) {
            bld = "";
        } else {
            bld = meta;
        }
        final int major = Integer.parseInt(matcher.group("major"));
        final int minor = Integer.parseInt(matcher.group("minor"));
        final int patch = Integer.parseInt(matcher.group("patch"));
        return new SemVer(major, minor, patch, pre, bld);
    }

    /**
     * Compare pre-release identifiers according to the SemVer specification.
     * <p>
     * When major, minor, and patch are equal, a version with pre-release
     * identifiers has lower precedence than a normal version. Pre-release
     * identifiers are compared in order: numeric are compared as integers,
     * non-numeric lexically. Numeric always have lower precedence than
     * non-numeric.
     * </p>
     *
     * @param left Left pre-release string
     * @param right Right pre-release string
     * @return Comparison result
     */
    @SuppressWarnings("PMD.CognitiveComplexity")
    private static int comparePre(final String left, final String right) {
        final int result;
        if (left.isEmpty() && right.isEmpty()) {
            result = 0;
        } else if (left.isEmpty()) {
            result = 1;
        } else if (right.isEmpty()) {
            result = -1;
        } else {
            result = SemVer.comparePreIdentifiers(
                left.split("\\."),
                right.split("\\.")
            );
        }
        return result;
    }

    /**
     * Compare arrays of pre-release identifiers according to SemVer rules.
     *
     * @param left Left identifier array
     * @param right Right identifier array
     * @return Comparison result
     * @checkstyle LocalVariableNameCheck (20 lines)
     */
    @SuppressWarnings({"PMD.UseVarargs", "PMD.CognitiveComplexity"})
    private static int comparePreIdentifiers(
        final String[] left, final String[] right
    ) {
        int result = 0;
        final int length = Math.min(left.length, right.length);
        for (int idx = 0; idx < length; idx += 1) {
            result = SemVer.compareIdentifier(left[idx], right[idx]);
            if (result != 0) {
                break;
            }
        }
        if (result == 0) {
            result = Integer.compare(left.length, right.length);
        }
        return result;
    }

    /**
     * Compare two individual pre-release identifiers.
     *
     * @param left Left identifier
     * @param right Right identifier
     * @return Comparison result
     */
    private static int compareIdentifier(
        final String left, final String right
    ) {
        final boolean lnum = SemVer.isNumericIdentifier(left);
        final boolean rnum = SemVer.isNumericIdentifier(right);
        final int result;
        if (lnum && rnum) {
            if (left.length() != right.length()) {
                result = Integer.compare(left.length(), right.length());
            } else {
                result = left.compareTo(right);
            }
        } else if (lnum) {
            result = -1;
        } else if (rnum) {
            result = 1;
        } else {
            result = left.compareTo(right);
        }
        return result;
    }

    /**
     * Determines if the identifier consists only of digits.
     *
     * @param identifier The identifier string
     * @return True if numeric, false otherwise
     * @checkstyle LocalVariableNameCheck (10 lines)
     */
    private static boolean isNumericIdentifier(final String identifier) {
        boolean digits = !identifier.isEmpty();
        for (int idx = 0; idx < identifier.length(); idx += 1) {
            if (!Character.isDigit(identifier.charAt(idx))) {
                digits = false;
                break;
            }
        }
        return digits;
    }
}
