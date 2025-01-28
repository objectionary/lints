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

import com.jcabi.log.Logger;
import com.jcabi.xml.Sources;
import com.jcabi.xml.XML;
import com.jcabi.xml.XSL;

/**
 * XSL that measures the time of transformation.
 * @since 0.1
 */
final class MeasuredXsl implements XSL {

    /**
     * Default threshold in milliseconds.
     */
    private static final long DEFAULT = 100L;

    /**
     * Rule name.
     */
    private final String rule;

    /**
     * Origin XSL.
     */
    private final XSL origin;

    /**
     * Custom threshold in milliseconds.
     */
    private final long threshold;

    /**
     * Ctor.
     * @param name Rule name.
     * @param decorated Decorated XSL.
     */
    MeasuredXsl(final String name, final XSL decorated) {
        this(name, decorated, MeasuredXsl.DEFAULT);
    }

    /**
     * Ctor.
     * @param name Rule name.
     * @param decorated Decorated XSL.
     * @param threshold Custom threshold in milliseconds.
     */
    private MeasuredXsl(final String name, final XSL decorated, final long threshold) {
        this.rule = name;
        this.origin = decorated;
        this.threshold = threshold;
    }

    @Override
    public XML transform(final XML xml) {
        final long start = System.currentTimeMillis();
        final XML res = this.origin.transform(xml);
        final long end = System.currentTimeMillis();
        if (end - start > this.threshold) {
            Logger.warn(
                this,
                "XSL transformation '%s' took %[ms]s, whereas threshold is %[ms]s\n",
                this.rule,
                end - start,
                this.threshold
            );
        }
        return res;
    }

    @Override
    public String applyTo(final XML xml) {
        return this.origin.applyTo(xml);
    }

    @Override
    public XSL with(final Sources src) {
        return this.origin.with(src);
    }

    @Override
    public XSL with(final String name, final Object value) {
        return this.origin.with(name, value);
    }
}
