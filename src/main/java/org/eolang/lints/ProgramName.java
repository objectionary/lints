/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.github.lombrozo.xnav.Filter;
import com.github.lombrozo.xnav.Xnav;
import com.jcabi.xml.XML;
import java.util.Optional;

/**
 * Program name extractor from XMIR.
 * Compatible with eo-parser 0.60.0+ which may produce XMIR without
 * object elements when there are parsing errors.
 *
 * @since 0.0.1
 */
final class ProgramName {

    /**
     * XML document.
     */
    private final Xnav xnav;

    /**
     * Ctor.
     * @param xml XML document
     */
    ProgramName(final XML xml) {
        this(new Xnav(xml.inner()));
    }

    /**
     * Ctor.
     * @param nav Navigator
     */
    private ProgramName(final Xnav nav) {
        this.xnav = nav;
    }

    /**
     * Get the program name.
     * @return Program name or "unknown" if not found
     */
    String get() {
        return this.xnav.element("object")
            .elements(Filter.withName("metas"))
            .findFirst()
            .map(this::packagedName)
            .orElseGet(() -> this.objectName().orElse("unknown"));
    }

    /**
     * Extract packaged name from metas element.
     * @param metas Metas element navigator
     * @return Full packaged name or just object name
     */
    private String packagedName(final Xnav metas) {
        return metas.elements(
            Filter.all(
                Filter.withName("meta"),
                meta -> new Xnav(meta)
                    .element("head")
                    .text()
                    .map("package"::equals)
                    .orElse(false)
            )
        )
        .findFirst()
        .map(
            meta -> meta.element("tail").text().map(
                pckg -> String.join(".", pckg, this.objectName().orElse("unknown"))
            ).orElseGet(() -> this.objectName().orElse("unknown"))
        )
        .orElseGet(() -> this.objectName().orElse("unknown"));
    }

    /**
     * Get object name from XMIR.
     * @return Object name or empty if not found
     */
    private Optional<String> objectName() {
        return this.xnav
            .element("object")
            .element("o")
            .attribute("name")
            .text()
            .or(
                () -> this.xnav
                    .element("object")
                    .element("class")
                    .attribute("name")
                    .text()
            );
    }
}
