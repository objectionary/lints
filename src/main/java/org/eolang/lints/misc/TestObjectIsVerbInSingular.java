/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2024 Objectionary.com
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
import org.eolang.lints.Defect;
import org.eolang.lints.Lint;
import org.eolang.lints.Severity;

/**
 * Lint that checks test object name is a verb in singular.
 *
 * @since 0.0.20
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public final class TestObjectIsVerbInSingular implements Lint {

    /**
     * The pattern to split kebab case.
     */
    private static final Pattern KEBAB = Pattern.compile("-");

    /**
     * The Open NLP tagger.
     */
    private final POSTaggerME model;

    /**
     * Ctor.
     *
     * @throws IOException if something went wrong.
     */
    public TestObjectIsVerbInSingular() throws IOException, URISyntaxException {
        this("https://opennlp.sourceforge.net/models-1.5/en-pos-perceptron.bin");
    }

    /**
     * Ctor.
     *
     * @param url Model URL
     * @throws IOException if something went wrong.
     */
    public TestObjectIsVerbInSingular(final String url) throws IOException, URISyntaxException {
        this(new POSModel(new URI(url).toURL()));
    }

    /**
     * Ctor.
     *
     * @param model POS model.
     */
    public TestObjectIsVerbInSingular(final POSModel model) {
        this(new POSTaggerME(model));
    }

    /**
     * Ctor.
     *
     * @param mdl The Open NLP tagger.
     */
    public TestObjectIsVerbInSingular(final POSTaggerME mdl) {
        this.model = mdl;
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
                            Arrays.stream(TestObjectIsVerbInSingular.KEBAB.split(name))
                        ).map(s -> s.toLowerCase(Locale.ROOT))
                        .toArray(String[]::new)
                )
            ).get(1);
            if (!("VB".equals(first) || "VBP".equals(first) || "VBZ".equals(first))) {
                defects.add(
                    new Defect.Default(
                        "test-object-is-not-verb-in-singular",
                        Severity.WARNING,
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
    public String motive() throws Exception {
        return new TextOf(
            new ResourceOf(
                "org/eolang/motives/misc/test-object-is-not-verb-in-singular.md"
            )
        ).asString();
    }
}
