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

import java.nio.file.Path;
import java.util.regex.Pattern;
import org.cactoos.Text;

/**
 * Relative path to XMIR file.
 * @since 0.0.30
 */
final class XmirKey implements Text {

    /**
     * XMIR extension.
     */
    private static final Pattern XMIR_EXT = Pattern.compile("\\.xmir$");

    /**
     * Path to .xmir file.
     */
    private final Path xmir;

    /**
     * Base path.
     */
    private final Path base;

    /**
     * Ctor.
     * @param xmr Path to .xmir file
     * @param bse Base path
     */
    XmirKey(final Path xmr, final Path bse) {
        this.xmir = xmr;
        this.base = bse;
    }

    @Override
    public String asString() {
        final Path parent = this.base.relativize(this.xmir.getParent());
        final Path path = this.xmir.getFileName();
        String fname = "";
        if (path != null) {
            fname = path.toString();
        }
        final String name = XmirKey.XMIR_EXT.matcher(fname).replaceAll("");
        final String key;
        if (parent.toString().isEmpty()) {
            key = name;
        } else {
            key = String.format("%s/%s", parent, name)
                .replace("\\", "/")
                .replace("/", ".");
        }
        return key;
    }
}
