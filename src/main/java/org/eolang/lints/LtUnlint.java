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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Lint that ignores linting if {@code +unlint} meta is present.
 *
 * @since 0.0.1
 */
final class LtUnlint implements Lint<XML> {

    /**
     * The original lint.
     */
    private final Lint<XML> origin;

    /**
     * Ctor.
     * @param lint The lint to decorate
     */
    LtUnlint(final Lint<XML> lint) {
        this.origin = lint;
    }

    @Override
    public String name() {
        return this.origin.name();
    }

    @Override
    public Collection<Defect> defects(final XML xmir) throws IOException {
        final boolean suppress = !xmir.nodes(
            String.format(
                "/program/metas/meta[head='unlint' and tail='%s']",
                this.origin.name()
            )
        ).isEmpty();
        final Collection<Defect> defects = new ArrayList<>(0);
        if (!suppress) {
            defects.addAll(this.origin.defects(xmir));
        }
        return defects;
    }

    @Override
    public String motive() throws IOException {
        return this.origin.motive();
    }

}
