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

import com.jcabi.log.Logger;
import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

/**
 * Lint that ignores linting if {@code +unlint} meta is present.
 *
 * @since 0.0.1
 */
final class LtWpaUnlint implements Lint<Map<String, XML>> {

    /**
     * The original lint.
     */
    private final Lint<Map<String, XML>> origin;

    /**
     * Ctor.
     * @param lint The lint to decorate
     */
    LtWpaUnlint(final Lint<Map<String, XML>> lint) {
        this.origin = lint;
    }

    @Override
    public String name() {
        return this.origin.name();
    }

    @Override
    public Collection<Defect> defects(final Map<String, XML> map) throws IOException {
        final Collection<Defect> defects = new LinkedList<>();
        for (final Defect defect : this.origin.defects(map)) {
            final XML xmir = map.get(defect.program());
            if (xmir == null) {
                throw new IllegalArgumentException(
                    Logger.format(
                        "The \"%s\" defect was found in \"%s\", but this program is not in scope (%[list]s), how come?",
                        defect.rule(), defect.program(), map.keySet()
                    )
                );
            }
            defects.addAll(new LtUnlint(new LtMono(defect)).defects(xmir));
        }
        return defects;
    }

    @Override
    public String motive() throws IOException {
        return this.origin.motive();
    }

}
