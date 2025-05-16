/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints.fix;

import com.github.lombrozo.xnav.Xnav;

public class Create {
    private final String path;

    private final boolean overwrite;

    private final boolean ignoreIfExists;

    public Create(String path, boolean overwrite, boolean ignoreIfExists) {
        this.path = path;
        this.overwrite = overwrite;
        this.ignoreIfExists = ignoreIfExists;
    }

    public Create(Xnav xnav) {
        this(
            xnav.element("path").text().get(),
            Boolean.parseBoolean(xnav.element("overwrite").text().orElse("")),
            Boolean.parseBoolean(xnav.element("ignoreIfExists").text().orElse(""))
        );
    }

    public String path() {
        return this.path;
    }

    public boolean overwrite() {
        return this.overwrite;
    }

    public boolean ignoreIfExists() {
        return this.ignoreIfExists;
    }
}
