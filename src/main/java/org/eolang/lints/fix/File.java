/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints.fix;

import com.github.lombrozo.xnav.Xnav;
import java.util.Arrays;
import java.util.Collection;

public class File {
    private final String path;
    private final Collection<Edit> edits;

    public File(final String path, final Edit... edits) {
        this.path = path;
        this.edits = Arrays.asList(edits);
    }

    public File(final Xnav xnav) {
        this(
            xnav.attribute("path").text().get(),
            xnav.path("edit").map(Edit::new).toArray(Edit[]::new)
        );
    }

    public String path() {
        return this.path;
    }

    public Collection<Edit> edits() {
        return this.edits;
    }
}
