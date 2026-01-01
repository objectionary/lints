/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
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
