/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

/**
 * Severity.
 *
 * @since 0.0.1
 */
public enum Severity {
    /**
     * Compilation can't continue, must stop.
     */
    CRITICAL("critical"),

    /**
     * It's a bug, must be fixed.
     */
    ERROR("error"),

    /**
     * Can live with it, but better be fixed.
     */
    WARNING("warning");

    /**
     * Name of it.
     */
    private final String name;

    /**
     * Ctor.
     * @param txt Name of it.
     */
    Severity(final String txt) {
        this.name = txt;
    }

    /**
     * Mnemo of it.
     * @return Mnemo
     */
    public String mnemo() {
        return this.name;
    }

    /**
     * Parse it from the text.
     *
     * @param text Text of it
     * @return Severity
     */
    @SuppressWarnings("PMD.ProhibitPublicStaticMethods")
    public static Severity parsed(final String text) {
        final Severity severity;
        if (Severity.CRITICAL.mnemo().equals(text)) {
            severity = Severity.CRITICAL;
        } else if (Severity.ERROR.mnemo().equals(text)) {
            severity = Severity.ERROR;
        } else if (Severity.WARNING.mnemo().equals(text)) {
            severity = Severity.WARNING;
        } else {
            throw new IllegalArgumentException(
                String.format("Can't parse '%s'", text)
            );
        }
        return severity;
    }
}
