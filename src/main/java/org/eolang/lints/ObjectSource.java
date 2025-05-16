/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.github.lombrozo.xnav.Xnav;
import com.jcabi.xml.XML;

final class ObjectSource {
    private final Xnav xnav;

    ObjectSource(XML xml) {
        this.xnav = new Xnav(xml.inner());
    }

    String get() {
        return xnav.attribute("source").text().orElse("");
    }
}
