/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import org.cactoos.io.InputStreamOf;
import org.cactoos.io.ResourceOf;

/**
 * English vocabulary checks for EO object names.
 *
 * <p>Uses <a href="https://opennlp.apache.org/">OpenNLP</a> POS tagging to
 * determine the grammatical role of words in a kebab-case name.</p>
 *
 * @since 0.2.0
 */
final class Vocabulary {

    /**
     * Pattern to split kebab-case names.
     */
    private static final Pattern KEBAB = Pattern.compile("-");

    /**
     * Part-Of-Speech tagger.
     */
    private final POSTaggerME tagger;

    /**
     * Lock guarding the tagger.
     */
    private final ReentrantLock lock;

    /**
     * Ctor.
     *
     * @throws IOException If fails to load the POS model resource
     */
    Vocabulary() throws IOException {
        this(Vocabulary.model());
    }

    /**
     * Ctor.
     *
     * @param mdl Part-Of-Speech model
     */
    Vocabulary(final POSModel mdl) {
        this(new POSTaggerME(mdl));
    }

    /**
     * Ctor.
     *
     * @param pos Part-Of-Speech tagger
     */
    Vocabulary(final POSTaggerME pos) {
        this.tagger = pos;
        this.lock = new ReentrantLock();
    }

    /**
     * Check if the given kebab-case name starts with a verb in third-person singular.
     *
     * <p>The check uses the "It [verb]s" rule: "It generates-report" -> the
     * first word must be tagged {@code VBZ} (verb, 3rd-person singular present).</p>
     *
     * @param name Kebab-case name without any leading {@code +} sigil
     * @return True if the first word is a VBZ-tagged verb
     */
    boolean isVerb(final String name) {
        this.lock.lock();
        try {
            return "VBZ".equals(
                this.tagger.tag(
                    Stream.concat(
                        Stream.of("It"),
                        Arrays.stream(Vocabulary.KEBAB.split(name))
                    ).map(s -> s.toLowerCase(Locale.ROOT)).toArray(String[]::new)
                )[1]
            );
        } finally {
            this.lock.unlock();
        }
    }

    private static POSModel model() throws IOException {
        try (InputStream stream = new InputStreamOf(new ResourceOf("en-pos-perceptron.bin"))) {
            return new POSModel(stream);
        }
    }
}
