/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.github.lombrozo.xnav.Xnav;
import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import org.cactoos.io.ResourceOf;
import org.cactoos.io.UncheckedInput;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;

/**
 * Lint that checks test object name is a verb in singular.
 * This lint uses <a href="https://opennlp.apache.org/">OpenNLP models</a>
 * with POS tagging capabilities in order to determine the part of speech and
 * tense for test object name.
 * @since 0.0.22
 */
final class LtTestNotVerb implements Lint<XML> {

    /**
     * The pattern to split kebab case.
     */
    private static final Pattern KEBAB = Pattern.compile("-");

    /**
     * Part-Of-Speech tagger.
     */
    private final POSTaggerME model;

    /**
     * Ctor.
     */
    LtTestNotVerb() {
        this(LtTestNotVerb.defaultPosModel());
    }

    /**
     * Ctor.
     * @param mdl Part-Of-Speech model
     */
    LtTestNotVerb(final POSModel mdl) {
        this(new POSTaggerME(mdl));
    }

    /**
     * Ctor.
     * @param pos Part-Of-Speech tagger
     */
    LtTestNotVerb(final POSTaggerME pos) {
        this.model = pos;
    }

    @Override
    public String name() {
        return "unit-test-is-not-verb";
    }

    @Override
    public Collection<Defect> defects(final XML xmir) throws IOException {
        return new Xnav(xmir.inner())
            .path("/object//o[@name and starts-with(@name, '+')]")
            .filter(object -> !this.isVerbInSingular(object))
            .map(object -> LtTestNotVerb.verbDefect(xmir, object))
            .collect(Collectors.toList());
    }

    @Override
    public String motive() throws IOException {
        return new UncheckedText(
            new TextOf(
                new ResourceOf(
                    "org/eolang/motives/misc/test-object-is-not-verb-in-singular.md"
                )
            )
        ).asString();
    }

    /**
     * Check if object name is verb in singular.
     * @param object Object navigator
     * @return True if first word is a verb in singular form
     */
    private boolean isVerbInSingular(final Xnav object) {
        return "VBZ".equals(
            this.model.tag(
                Stream
                    .concat(
                        Stream.of("It"),
                        Arrays.stream(
                            LtTestNotVerb.KEBAB.split(
                                object.attribute("name").text().get().replace("+", "")
                            )
                        )
                    )
                    .map(s -> s.toLowerCase(Locale.ROOT))
                    .toArray(String[]::new)
            )[1]
        );
    }

    /**
     * Create defect for non-verb test name.
     * @param xmir Source XML
     * @param object Object navigator
     * @return Defect
     */
    private static Defect verbDefect(final XML xmir, final Xnav object) {
        return new Defect.Default(
            "unit-test-is-not-verb",
            Severity.WARNING,
            new ProgramName(xmir).get(),
            Integer.parseInt(object.attribute("line").text().orElse("0")),
            String.format(
                "Test object name: \"%s\" doesn't start with verb in singular form",
                object.attribute("name").text().get().replace("+", "")
            )
        );
    }

    private static POSModel defaultPosModel() {
        try {
            return new POSModel(
                new UncheckedInput(
                    new ResourceOf("en-pos-perceptron.bin")
                ).stream()
            );
        } catch (final IOException exception) {
            throw new IllegalStateException(
                "Failed to read from I/O", exception
            );
        }
    }
}
