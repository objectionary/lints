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
package org.eolang.lints;

import java.io.IOException;
import java.util.Collection;

import com.jcabi.xml.XML;
import io.github.secretx33.resourceresolver.ClassPathResource;
import org.cactoos.text.FormattedText;

public class JavaLint implements Lint<XML> {
    private final Lint<XML> lint;

    public JavaLint(Lint<XML> lint) {
        this.lint = lint;
    }

    @Override
    public Collection<Defect> defects(XML entity) throws IOException {
        return lint.defects(entity);
    }

    @Override
    public String motive() throws Exception {
        String className = lint.getClass()
                .getSimpleName()
                .replaceAll("([a-z0-9])([A-Z])", "$1-$2")
                .toLowerCase();
        String packageName = lint.getClass().getPackage().getName();
        packageName = packageName.substring(packageName.lastIndexOf(".") + 1);
        return new ClassPathResource(
                new FormattedText("org/eolang/motives/%s/%s.md",
                        packageName,
                        className).asString()).getURL().toString();
    }

}
