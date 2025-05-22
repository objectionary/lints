/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.github.lombrozo.xnav.Xnav;
import org.cactoos.Scalar;
import org.w3c.dom.Node;

/**
 * Parent object to the given object.
 * @since 0.0.50
 */
final class ParentObject implements Scalar<Xnav> {

    /**
     * Current object.
     */
    private final Xnav current;

    /**
     * Ctor.
     * @param obj The object
     */
    ParentObject(final Xnav obj) {
        this.current = obj;
    }

    @Override
    public Xnav value() {
        final Xnav result;
        final Node prev = this.current.node().getParentNode();
        if (prev != null && (int) prev.getNodeType() == (int) Node.ELEMENT_NODE) {
            result = new Xnav(prev);
        } else {
            result = new Xnav("<o/>");
        }
        return result;
    }
}
