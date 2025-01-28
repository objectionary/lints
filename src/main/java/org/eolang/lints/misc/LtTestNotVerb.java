/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2025 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.eolang.lints.misc;

import com.jcabi.xml.XML;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import org.cactoos.io.ResourceOf;
import org.cactoos.list.ListOf;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;
import org.eolang.lints.Defect;
import org.eolang.lints.Lint;
import org.eolang.lints.Severity;

/**
 * Lint that checks test object name is a verb in singular.
 * This lint uses <a href="https://opennlp.apache.org/">OpenNLP models</a>
 * with POS tagging capabilities in order to determine the part of speech and
 * tense for test object name.
 * @since 0.0.22
 * @todo #257:60min Configure model download only during the build and place into the JAR.
 *  Currently, we download model file each time when creating the lint, which may
 *  be slow in the usage of this lint. Instead, let's configure maven to download
 *  model file during the build, and place into JAR, so lint will be able to locate
 *  file from resources faster.
 */
public final class LtTestNotVerb implements Lint<XML> {

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
    public LtTestNotVerb() {
        this(LtTestNotVerb.defaultPosModel());
    }

    /**
     * Ctor.
     * @param mdl Part-Of-Speech model
     */
    public LtTestNotVerb(final POSModel mdl) {
        this(new POSTaggerME(mdl));
    }

    /**
     * Ctor.
     * @param pos Part-Of-Speech tagger
     */
    public LtTestNotVerb(final POSTaggerME pos) {
        this.model = pos;
    }

    @Override
    public Collection<Defect> defects(final XML xmir) throws IOException {
        final Collection<Defect> defects = new LinkedList<>();
        for (final XML object : xmir.nodes("/program[metas/meta[head='tests']]/objects/o[@name]")) {
            final String name = object.xpath("@name").get(0);
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
                        xmir.xpath("/program/@name").stream().findFirst().orElse("unknown"),
                        Integer.parseInt(object.xpath("@line").get(0)),
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
                new URI("https://opennlp.sourceforge.net/models-1.5/en-pos-perceptron.bin")
                    .toURL()
            );
        } catch (final IOException exception) {
            throw new IllegalStateException(
                "Failed to read from I/O", exception
            );
        } catch (final URISyntaxException exception) {
            throw new IllegalStateException(
                "URI syntax is broken", exception
            );
        }
    }
}
