/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints.fix;

import com.github.lombrozo.xnav.Xnav;

public class Position {
    private final int line;

    private final int character;

    public Position(final int line, final int character) {
        this.line = line;
        this.character = character;
    }

    public Position(Xnav xnav) {
        this(
            Integer.parseInt(xnav.attribute("line").text().get()),
            Integer.parseInt(xnav.attribute("character").text().get())
        );
    }

    public int line() {
        return this.line;
    }

    public int character() {
        return this.character;
    }
}
