/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import com.yegor256.xsline.Shift;
import com.yegor256.xsline.Xsline;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import org.eolang.xax.Xtory;
import org.xembly.Directives;
import org.xembly.Xembler;

/**
 * A story that runs a Java-implemented lint by its name.
 * Reads the {@code lint} key from the pack YAML and applies
 * the resolved {@link Lint} to the input, producing a
 * {@code <defects>} document.
 * @since 1.0
 */
public final class XtLint implements Xtory {

    /**
     * Original story.
     */
    private final Xtory origin;

    /**
     * Ctor.
     * @param yaml YAML pack
     * @param parser Parser
     */
    public XtLint(final String yaml, final Xtory.Parser parser) {
        this(new org.eolang.xax.XtYaml(yaml, parser));
    }

    /**
     * Ctor.
     * @param origin Original story
     */
    private XtLint(final Xtory origin) {
        this.origin = origin;
    }

    @Override
    public Map<String, Object> map() {
        return this.origin.map();
    }

    @Override
    public XML before() {
        return this.origin.before();
    }

    @Override
    public XML after() {
        return this.xsline().pass(this.before());
    }

    @Override
    public Xsline xsline() {
        return new Xsline(
            new Shift() {
                @Override
                public String uid() {
                    return XtLint.this.name();
                }

                @Override
                public XML apply(final int position, final XML xml) {
                    return XtLint.this.defects(xml);
                }
            }
        );
    }

    @Override
    public Collection<String> asserts() {
        return this.origin.asserts();
    }

    /**
     * Lint name from the YAML map.
     * @return Lint name
     */
    private String name() {
        return String.valueOf(this.origin.map().get("lint"));
    }

    /**
     * Run the lint and serialize defects into XML.
     * @param xml Input XMIR
     * @return Defects document
     */
    private XML defects(final XML xml) {
        final Directives dirs = new Directives().add("defects");
        try {
            for (final Lint lint : new PkMono()) {
                if (lint.name().equals(this.name())) {
                    for (final Defect defect : lint.defects(xml)) {
                        dirs.add("defect")
                            .attr("line", defect.line())
                            .attr("severity", defect.severity().mnemo())
                            .set(defect.text())
                            .up();
                    }
                    break;
                }
            }
        } catch (final IOException ex) {
            throw new IllegalStateException(
                String.format("Failed to run lint %s", this.name()),
                ex
            );
        }
        return new com.jcabi.xml.XMLDocument(
            new Xembler(dirs).xmlQuietly()
        );
    }
}
