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
    public String program() {
        return this.origin.program();
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
    public String toString() {
        return this.origin.toString();
    }
}
