/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import com.yegor256.xsline.Shift;
import com.yegor256.xsline.Xsline;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.stream.StreamSupport;
import org.eolang.xax.XtYaml;
import org.eolang.xax.Xtory;
import org.xembly.Directives;
import org.xembly.Xembler;

/**
 * A story that runs a Java-implemented lint by its name.
 * Reads the {@code lint} key from the pack YAML and applies
 * the resolved {@link Lint} to the input, producing a
 * {@code <defects>} document. The input itself may be given either
 * as a raw XMIR document (the {@code document} pack key) or as
 * EO source (the {@code input} pack key), same as {@link XtYaml}.
 * By default, the lint is taken as-is from {@link PkMono} (its
 * default, no-arg construction). A pack may instead supply a
 * generic {@code params} map; when present, {@link #lint()} builds
 * the matching lint from those parameters directly, instead of
 * using the default one from {@link PkMono}.
 *
 * @since 1.0
 */
public final class XtLint implements Xtory {

    /**
     * Original story.
     */
    private final Xtory origin;

    /**
     * Ctor.
     *
     * @param yaml YAML pack
     * @param parser Parser
     */
    public XtLint(final String yaml, final Xtory.Parser parser) {
        this(new XtYaml(yaml, parser));
    }

    /**
     * Ctor.
     *
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

    private String name() {
        return String.valueOf(this.origin.map().get("lint"));
    }

    private XML defects(final XML xml) {
        final Directives dirs = new Directives().add("defects");
        try {
            for (final Defect defect : this.lint().defects(xml)) {
                dirs.add("defect")
                    .attr("line", defect.line())
                    .attr("rule", defect.rule())
                    .attr("severity", defect.severity().mnemo())
                    .set(defect.text())
                    .up();
            }
        } catch (final IOException ex) {
            throw new IllegalStateException(
                String.format("Failed to run lint %s", this.name()),
                ex
            );
        }
        return new XMLDocument(
            new Xembler(dirs).xmlQuietly()
        );
    }

    @SuppressWarnings("unchecked")
    private Lint lint() {
        final Lint result;
        if (this.origin.map().containsKey("params")) {
            if ("reserved-name".equals(this.name())) {
                result = new LtReservedName(
                    (Map<String, String>) this.origin.map().get("params")
                );
            } else {
                throw new IllegalStateException(
                    String.format(
                        "Lint '%s' does not support the 'params' pack key",
                        this.name()
                    )
                );
            }
        } else {
            result = StreamSupport.stream(new PkMono().spliterator(), false)
                .filter(lint -> lint.name().equals(this.name()))
                .findFirst().orElseThrow(
                    () -> new IllegalStateException(
                        String.format("Lint '%s' is not found in PkMono", this.name())
                    )
                );
        }
        return result;
    }
}
