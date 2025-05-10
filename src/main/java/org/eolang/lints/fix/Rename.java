/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints.fix;

import com.github.lombrozo.xnav.Xnav;

public class Rename {
    private final String oldPath;
    private final String newPath;
    private final boolean overwrite;
    private final boolean ignoreIfExists;

    public Rename(String oldPath, String newPath, boolean overwrite, boolean ignoreIfExists) {
        this.oldPath = oldPath;
        this.newPath = newPath;
        this.overwrite = overwrite;
        this.ignoreIfExists = ignoreIfExists;
    }

    public Rename(Xnav xnav) {
        this(
            xnav.element("oldPath").text().get(),
            xnav.element("newPath").text().get(),
            Boolean.parseBoolean(xnav.element("overwrite").text().orElse("")),
            Boolean.parseBoolean(xnav.element("ignoreIfExists").text().orElse(""))
        );
    }

    public String oldPath() {
        return this.oldPath;
    }

    public String newPath() {
        return this.newPath;
    }

    public boolean overwrite() {
        return this.overwrite;
    }

    public boolean ignoreIfExists() {
        return this.ignoreIfExists;
    }
}
