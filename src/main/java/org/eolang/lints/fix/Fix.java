/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints.fix;

import com.github.lombrozo.xnav.Xnav;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Fix {
    private final Collection<File> files;

    private final Collection<Create> creates;

    private final Collection<Delete> deletes;

    private final Collection<Rename> renames;

    public Fix(Collection<File> files, Collection<Create> creates, Collection<Delete> deletes, Collection<Rename> renames) {
        this.files = files;
        this.creates = creates;
        this.deletes = deletes;
        this.renames = renames;
    }

    public Fix() {
        this(
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList()
        );
    }

    public Fix(File file) {
        this(
            Collections.singletonList(file),
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList()
        );
    }

    public Fix(Xnav fix) {
        this(
            fix.path("file").map(File::new).collect(Collectors.toList()),
            fix.path("create").map(Create::new).collect(Collectors.toList()),
            fix.path("delete").map(Delete::new).collect(Collectors.toList()),
            fix.path("rename").map(Rename::new).collect(Collectors.toList())
        );
    }

    public Collection<File> files() {
        return this.files;
    }

    public Collection<Create> creates() {
        return this.creates;
    }

    public Collection<Delete> deletes() {
        return this.deletes;
    }

    public Collection<Rename> renames() {
        return this.renames;
    }
}
