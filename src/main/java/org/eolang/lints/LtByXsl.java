/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.github.lombrozo.xnav.Filter;
import com.github.lombrozo.xnav.Xnav;
import com.jcabi.xml.ClasspathSources;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import com.jcabi.xml.XSL;
import com.jcabi.xml.XSLDocument;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import org.cactoos.Input;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.IoCheckedText;
import org.cactoos.text.TextOf;
import org.eolang.parser.ObjectName;

/**
 * Lint by XSL.
 *
 * @since 0.0.1
 */
final class LtByXsl implements Lint<XML> {

    /**
     * The name of the rule.
     */
    private final String rule;

    /**
     * The stylesheet.
     */
    private final XSL sheet;

    /**
     * Motive document.
     */
    private final Input doc;

    /**
     * Ctor.
     * @param xsl Relative path of XSL
     * @throws IOException If fails
     */
    LtByXsl(final String xsl) throws IOException {
        this(
            new ResourceOf(
                String.format("org/eolang/lints/%s.xsl", xsl)
            ),
            new ResourceOf(
                String.format("org/eolang/motives/%s.md", xsl)
            )
        );
    }

    /**
     * Ctor.
     * @param xsl Relative path of XSL
     * @param motive Relative path of a motive document
     * @throws IOException If fails
     */
    @SuppressWarnings("PMD.ConstructorOnlyInitializesOrCallOtherConstructors")
    LtByXsl(final Input xsl, final Input motive) throws IOException {
        final XML xml = new XMLDocument(new IoCheckedText(new TextOf(xsl)).asString());
        this.rule = new Xnav(xml.toString())
            .element("xsl:stylesheet")
            .attribute("id")
            .text()
            .orElseThrow();
        this.sheet = new MeasuredXsl(
            this.rule,
            new XSLDocument(xml, this.rule).with(new ClasspathSources())
        );
        this.doc = motive;
    }

    @Override
    public String name() {
        return this.rule;
    }

    @Override
    public Collection<Defect> defects(final XML xmir) {
        final XML report = this.sheet.transform(xmir);
        final Collection<Defect> defects = new ArrayList<>(0);
        for (final XML defect : LtByXsl.findDefects(report)) {
            final Xnav xml = new Xnav(defect.inner());
            final Optional<String> sever = xml.attribute("severity").text();
            if (sever.isEmpty()) {
                throw new IllegalStateException(
                    String.format("No severity reported by %s", this.rule)
                );
            }
            defects.add(
                new DfContext(
                    new Defect.Default(
                        this.rule,
                        Severity.parsed(sever.get()),
                        new ObjectName(xmir).get(),
                        this.lineno(xml),
                        xml.text().get(),
                        LtByXsl.experimental(xml)
                    ),
                    xml.attribute("context").text().orElse("")
                )
            );
        }
        return defects;
    }

    @Override
    public String motive() throws IOException {
        return new IoCheckedText(new TextOf(this.doc)).asString();
    }

    /**
     * Defect is experimental?
     * @param defect Defect
     * @return Experimental or not
     */
    private static boolean experimental(final Xnav defect) {
        final boolean result;
        final Optional<String> attr = defect.attribute("experimental").text();
        if (attr.isEmpty()) {
            result = false;
        } else {
            result = Boolean.parseBoolean(attr.get());
        }
        return result;
    }

    /**
     * Get line number of the defect.
     * @param defect XML defect
     * @return Line number
     */
    private int lineno(final Xnav defect) {
        final Optional<String> oline = defect.attribute("line").text();
        if (oline.isEmpty()) {
            throw new IllegalStateException(
                String.format("No line number reported by %s", this.rule)
            );
        }
        final String line = oline.get();
        if (line.isEmpty()) {
            throw new IllegalStateException(
                String.format("Empty line number reported by %s", this.rule)
            );
        }
        final int lineno;
        try {
            lineno = Integer.parseInt(line);
        } catch (final NumberFormatException ex) {
            throw new IllegalStateException(
                String.format(
                    "Wrong line number reported by %s: '%s'",
                    this.rule, line
                ),
                ex
            );
        }
        return lineno;
    }

    /**
     * Find defects in the report.
     * @param report XML report.
     * @return Collection of defects.
     */
    private static Collection<XML> findDefects(final XML report) {
        return new Xnav(report.inner())
            .element("defects")
            .elements(Filter.withName("defect"))
            .map(Xnav::copy)
            .map(Xnav::node)
            .map(XMLDocument::new)
            .collect(Collectors.toList());
    }
}
