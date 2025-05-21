/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

/**
 * Defect with context.
 * @since 0.0.40
 */
final class DfContext implements Defect {

    /**
     * Origin defect.
     */
    private final Defect origin;

    /**
     * Context.
     */
    private final String ctxt;

    /**
     * Ctor.
     * @param orgn Origin defect
     * @param context Context
     */
    DfContext(final Defect orgn, final String context) {
        this.origin = orgn;
        this.ctxt = context;
    }

    @Override
    public String rule() {
        return this.origin.rule();
    }

    @Override
    public Severity severity() {
        return this.origin.severity();
    }

    @Override
    public String object() {
        return this.origin.object();
    }

    @Override
    public int line() {
        return this.origin.line();
    }

    @Override
    public String text() {
        return this.origin.text();
    }

    @Override
    public String version() {
        return this.origin.version();
    }

    @Override
    public String context() {
        return this.ctxt.replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&#34;", "\"")
            .replace("&#xA;", "\n")
            .replace("&amp;", "&");
    }

    @Override
    public boolean experimental() {
        return this.origin.experimental();
    }

    @Override
    public String toString() {
        return this.origin.toString();
    }
}
