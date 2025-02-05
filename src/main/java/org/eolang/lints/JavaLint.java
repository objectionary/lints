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
package org.eolang.lints;

import com.jcabi.xml.XML;
import io.github.secretx33.resourceresolver.ClassPathResource;
import java.io.IOException;
import java.util.Collection;
import java.util.Locale;
import org.cactoos.text.FormattedText;
import org.cactoos.text.IoCheckedText;

/**
 * Override {@link Lint#motive()} method. Overridden method will return path to the file on the
 * classpath with the same name as the class of the decorated object but in camel-case.
 *
 * @since 0.0.0
 */
public final class JavaLint implements Lint<XML> {
    /**
     * Decorated object.
     */
    private final Lint<XML> lint;

    public JavaLint(final Lint<XML> lint) {
        this.lint = lint;
    }

    @Override
    public String name() {
        return this.lint.name();
    }

    @Override
    public Collection<Defect> defects(final XML entity) throws IOException {
        return this.lint.defects(entity);
    }

    @Override
    public String motive() throws IOException {
        final String classname = this.lint.getClass()
            .getSimpleName()
            .replaceAll("([a-z0-9])([A-Z])", "$1-$2")
            .toLowerCase(Locale.ROOT);
        String packagename = this.lint.getClass().getPackage().getName();
        packagename = packagename.substring(packagename.lastIndexOf('.') + 1);
        return new ClassPathResource(
            new IoCheckedText(
                new FormattedText(
                    "org/eolang/motives/%s/%s.md",
                    packagename,
                    classname
                )
            ).asString()
        )
        .getURL()
        .toString();
    }

}
