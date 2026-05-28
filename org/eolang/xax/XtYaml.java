/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2025 Yegor Bugayenko
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
package org.eolang.xax;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import com.jcabi.xml.XSLDocument;
import com.yegor256.xsline.Shift;
import com.yegor256.xsline.StClasspath;
import com.yegor256.xsline.StXSL;
import com.yegor256.xsline.TrDefault;
import com.yegor256.xsline.Train;
import com.yegor256.xsline.Xsline;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

/**
 * A story parsed from YAML and then processed through XSL
 * stylesheets.
 *
 * @since 0.1.0
 */
public final class XtYaml implements Xtory {

    /**
     * The YAML to work with.
     */
    private final String yaml;

    /**
     * The train to start with.
     */
    private final Train<Shift> train;

    /**
     * The parser to use, when {@code input} is provided in the YAML.
     */
    private final Parser parser;

    /**
     * Ctor.
     * @param yml The story in YAML
     */
    public XtYaml(final String yml) {
        this(
            yml,
            input -> {
                throw new UnsupportedOperationException(
                    "Parser is not provided, while YAML doesn't have the 'document' property"
                );
            }
        );
    }

    /**
     * Ctor.
     * @param yml The story in YAML
     * @param prsr The parser to use
     */
    public XtYaml(final String yml, final Parser prsr) {
        this(yml, prsr, new TrDefault<>());
    }

    /**
     * Ctor.
     * @param yml The story in YAML
     * @param prsr The parser to use
     * @param trn The train to start with
     * @since 0.1.1
     */
    public XtYaml(final String yml, final Parser prsr, final Train<Shift> trn) {
        this.yaml = yml;
        this.parser = prsr;
        this.train = trn;
    }

    @Override
    public Map<String, Object> map() {
        return new Yaml().load(
            String.class.cast(this.yaml)
        );
    }

    @Override
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public XML before() {
        final Object doc = this.map().get("document");
        final XML xml;
        if (doc == null) {
            final Object input = this.map().get("input");
            if (input == null) {
                throw new IllegalArgumentException(
                    "Neither 'document' nor 'input' exists in the YAML"
                );
            }
            try {
                xml = this.parser.parse(input.toString());
                // @checkstyle IllegalCatchCheck (1 line)
            } catch (final Exception ex) {
                throw new IllegalArgumentException(ex);
            }
        } else {
            xml = new XMLDocument(doc.toString());
        }
        return xml;
    }

    @Override
    public XML after() {
        return this.xsline().pass(this.before());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Xsline xsline() {
        Object sheets = this.map().get("sheets");
        if (sheets == null) {
            sheets = Arrays.asList();
        }
        Train<Shift> trn = this.train;
        for (final String sheet : (Iterable<String>) sheets) {
            if (sheet.startsWith("file://")) {
                try {
                    trn = trn.with(
                        new StXSL(
                            new XSLDocument(Paths.get(sheet.substring(7)))
                        )
                    );
                } catch (final FileNotFoundException ex) {
                    throw new IllegalArgumentException(ex);
                }
            } else {
                trn = trn.with(new StClasspath(sheet));
            }
        }
        return new Xsline(trn);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<String> asserts() {
        Object asserts = this.map().get("asserts");
        if (asserts == null) {
            asserts = Arrays.asList();
        }
        final Collection<String> xpaths = new LinkedList<>();
        for (final String xpath : (Iterable<String>) asserts) {
            xpaths.add(xpath);
        }
        return xpaths;
    }

}
