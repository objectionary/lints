/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

/**
 * A single fenced "Incorrect:"/"Correct:" code example extracted from a
 * lint's motive text by {@link Motives}.
 *
 * @since 0.0.1
 */
final class Example {

    /**
     * Fence language, "eo" or "xml".
     */
    private final String lng;

    /**
     * Fenced content.
     */
    private final String txt;

    /**
     * Is this a "Correct:" example (as opposed to "Incorrect:")?
     */
    private final boolean valid;

    /**
     * Ctor.
     *
     * @param lang Fence language, "eo" or "xml"
     * @param body Fenced content
     * @param correct Is this a "Correct:" example?
     */
    Example(final String lang, final String body, final boolean correct) {
        this.lng = lang;
        this.txt = body;
        this.valid = correct;
    }

    /**
     * Fence language, "eo" or "xml".
     *
     * @return Language
     */
    String lang() {
        return this.lng;
    }

    /**
     * Fenced content.
     *
     * @return Body
     */
    String body() {
        return this.txt;
    }

    /**
     * Is this a "Correct:" example (as opposed to "Incorrect:")?
     *
     * @return True if correct
     */
    boolean correct() {
        return this.valid;
    }

    /**
     * The label of the section this example was found under.
     *
     * @return Either "Correct" or "Incorrect"
     */
    String label() {
        final String result;
        if (this.valid) {
            result = "Correct";
        } else {
            result = "Incorrect";
        }
        return result;
    }
}
