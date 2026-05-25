/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.github.lombrozo.xnav.Xnav;
import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

/**
 * Lint that checks test object name is a verb in singular.
 * This lint uses <a href="https://opennlp.apache.org/">OpenNLP models</a>
 * with POS tagging capabilities in order to determine the part of speech and
 * tense for test object name.
 * @since 0.0.22
 */
final class LtTestNotVerb implements Lint {

    /**
     * Vocabulary for English name checks.
     */
    private final Vocabulary vocabulary;

    /**
     * Ctor.
     * @throws IOException If fails
     */
    LtTestNotVerb() throws IOException {
        this(new Vocabulary());
    }

    /**
     * Ctor.
     * @param vocab Vocabulary to use for name checks
     */
    LtTestNotVerb(final Vocabulary vocab) {
        this.vocabulary = vocab;
    }

    @Override
    public String name() {
        return "unit-test-is-not-verb";
    }

    @Override
    public Collection<Defect> defects(final XML xmir) throws IOException {
        return new Xnav(xmir.inner())
            .path("/object//o[@name and starts-with(@name, '+')]")
            .filter(
                object -> !this.vocabulary.isVerb(
                    object.attribute("name").text().get().replace("+", "")
                )
            )
            .map(LtTestNotVerb::verbDefect)
            .collect(Collectors.toList());
    }

    @Override
    public String motive() throws IOException {
        return new MotiveFrom("misc", "test-object-is-not-verb-in-singular").asString();
    }

    /**
     * Create defect for non-verb test name.
     * @param object Object navigator
     * @return Defect
     */
    private static Defect verbDefect(final Xnav object) {
        return new Defect.Default(
            "unit-test-is-not-verb",
            Severity.WARNING,
            new LineOf(object).value(),
            String.format(
                "Test object name: \"%s\" doesn't start with verb in singular form",
                object.attribute("name").text().get().replace("+", "")
            )
        );
    }
}
