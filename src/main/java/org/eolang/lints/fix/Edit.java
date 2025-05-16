/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints.fix;

import com.github.lombrozo.xnav.Xnav;

public class Edit {
    private final Position start;

    private final Position end;

    private final String newText;

    public Edit(final Position start, final Position end, final String newText) {
        this.start = start;
        this.end = end;
        this.newText = newText;
    }

    public Edit(Xnav xnav) {
        this(
            new Position(xnav.element("start")),
            new Position(xnav.element("end")),
            xnav.element("newText").text().get()
        );
    }

    public Position start() {
        return this.start;
    }

    public Position end() {
        return this.end;
    }

    public String newText() {
        return this.newText;
    }
}
