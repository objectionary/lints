/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
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
