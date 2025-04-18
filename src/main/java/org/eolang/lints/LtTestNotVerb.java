/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.github.lombrozo.xnav.Xnav;
import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import org.cactoos.io.ResourceOf;
import org.cactoos.io.UncheckedInput;
import org.cactoos.list.ListOf;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;
import org.eolang.parser.ObjectName;

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
    public Collection<Defect> defects(final XML xmir) throws IOException {
        final Collection<Defect> defects = new LinkedList<>();
        final Xnav xml = new Xnav(xmir.inner());
        final List<Xnav> objects = xml
            .path("/object[metas/meta[head='tests']]/o[@name]")
            .collect(Collectors.toList());
        for (final Xnav object : objects) {
            final String name = object.attribute("name").text().get();
            final String first = new ListOf<>(
                this.model.tag(
                    Stream
                        .concat(
                            Stream.of("It"),
                            Arrays.stream(LtTestNotVerb.KEBAB.split(name))
                        )
                        .map(s -> s.toLowerCase(Locale.ROOT))
                        .toArray(String[]::new)
                )
            ).get(1);
            if (!"VBZ".equals(first)) {
                defects.add(
                    new Defect.Default(
                        "unit-test-is-not-verb",
                        Severity.WARNING,
                        new ObjectName(xmir).get(),
                        Integer.parseInt(object.attribute("line").text().orElse("0")),
                        String.format(
                            "Test object name: \"%s\" doesn't start with verb in singular form",
                            name
                        )
                    )
                );
            }
        }
        return defects;
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

    @Override
    public String name() {
        return "unit-test-is-not-verb";
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
