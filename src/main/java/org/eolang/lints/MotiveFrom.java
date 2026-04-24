/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import java.io.IOException;
import org.cactoos.Input;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.IoCheckedText;
import org.cactoos.text.TextOf;

/**
 * Motive document loaded from a classpath resource.
 *
 * <p>Every {@link Lint} in this package exposes a motive markdown that
 * lives under {@code org/eolang/motives/}. This small helper centralizes
 * the loading so lints can just say {@code new MotiveFrom(dimension, name).asString()}
 * instead of reinventing the cactoos text wrapping each time.</p>
 *
 * @since 0.0.50
 */
final class MotiveFrom {

    /**
     * Input of the motive document.
     */
    private final Input input;

    /**
     * Ctor, picking the document by dimension and lint name.
     * @param dimension Motive dimension (e.g. {@code "errors"}, {@code "misc"}, {@code "names"})
     * @param lint Lint name, used as the markdown file basename
     */
    MotiveFrom(final String dimension, final String lint) {
        this(
            new ResourceOf(
                String.format("org/eolang/motives/%s/%s.md", dimension, lint)
            )
        );
    }

    /**
     * Ctor.
     * @param src Raw input of the motive document
     */
    MotiveFrom(final Input src) {
        this.input = src;
    }

    /**
     * Read the motive document as a string.
     * @return Motive content
     * @throws IOException If reading the resource fails
     */
    String asString() throws IOException {
        return new IoCheckedText(new TextOf(this.input)).asString();
    }
}
