/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import java.util.Optional;

/**
 * The full source code of the program, as stored in the XMIR
 * {@code <listing>} element. It helps to find the real source line
 * of a comment, which is not stored directly in XMIR.
 *
 * <p>The parser merges a multi-line comment into a single
 * {@code <comment>} node with {@code @line} pointing at the last line
 * of the block. To get the exact line of a character inside the
 * comment, this class locates the comment in the listing (where every
 * line starts with {@code #}) and counts the newlines up to the
 * character position.</p>
 *
 * @since 0.0.53
 */
final class Listing {

    /**
     * The full source code of the program.
     */
    private final String source;

    /**
     * Ctor.
     * @param src Full source code of the program
     */
    Listing(final String src) {
        this.source = src;
    }

    /**
     * Real source line of the character at the given position in the
     * comment text.
     * @param text Comment text
     * @param pos Position of the character inside the comment text
     * @return Real source line, or empty when the comment is not found
     */
    Optional<Integer> line(final String text, final int pos) {
        final int newline = text.indexOf('\n');
        final String headline;
        if (newline < 0) {
            headline = text;
        } else {
            headline = text.substring(0, newline);
        }
        final int index = this.source.indexOf("# ".concat(headline));
        final Optional<Integer> result;
        if (index < 0) {
            result = Optional.empty();
        } else {
            result = Optional.of(
                (int) this.source.substring(0, index).chars()
                    .filter(chr -> chr == '\n').count()
                    + 1 + (int) text.substring(0, pos).chars()
                    .filter(chr -> chr == '\n').count()
            );
        }
        return result;
    }
}
