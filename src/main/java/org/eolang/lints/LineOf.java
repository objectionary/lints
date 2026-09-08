/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.github.lombrozo.xnav.Xnav;

/**
 * Line number attached to an XML element via its {@code @line} attribute.
 *
 * <p>Most hand-coded lints read the line number off an XMIR element with the
 * same fallback pattern: when the attribute is missing, they report line 0.
 * This class captures that convention in one place so individual lints don't
 * repeat the {@code Integer.parseInt(...orElse("0"))} litany. For callers that
 * need stricter handling (e.g. {@link LtByXsl}), prefer a bespoke parser.</p>
 *
 * @since 0.0.50
 */
final class LineOf {

    /**
     * XML element that carries the {@code @line} attribute.
     */
    private final Xnav element;

    /**
     * Ctor.
     * @param elem XML element to read from
     */
    LineOf(final Xnav elem) {
        this.element = elem;
    }

    /**
     * Line number, or {@code 0} when the attribute is missing or not an integer.
     * @return Line number
     */
    int value() {
        final String line = this.element.attribute("line").text().orElse("0");
        final int result;
        if (line.matches("\\d+")) {
            result = Integer.parseInt(line);
        } else {
            result = 0;
        }
        return result;
    }
}
