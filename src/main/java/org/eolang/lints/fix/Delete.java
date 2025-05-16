/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints.fix;

import com.github.lombrozo.xnav.Xnav;

public class Delete {
    private final String path;

    private final boolean recursive;

    private final boolean ignoreIfNotExists;

    public Delete(String path, boolean recursive, boolean ignoreIfNotExists) {
        this.path = path;
        this.recursive = recursive;
        this.ignoreIfNotExists = ignoreIfNotExists;
    }

    public Delete(final Xnav xnav) {
        this(
            xnav.element("path").text().get(),
            Boolean.parseBoolean(xnav.element("recursive").text().orElse("")),
            Boolean.parseBoolean(xnav.element("ignoreIfNotExists").text().orElse(""))
        );
    }

    public String path() {
        return this.path;
    }

    public boolean recursive() {
        return this.recursive;
    }

    public boolean ignoreIfNotExists() {
        return this.ignoreIfNotExists;
    }
}
