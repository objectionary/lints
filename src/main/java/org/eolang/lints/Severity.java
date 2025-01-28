/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2025 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
