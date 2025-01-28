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
import java.util.Collection;
import java.util.Collections;

/**
 * A single checker for an {@code .xmir} file.
 *
 * @param <T> The type of entity to analyze
 * @since 0.0.1
 */
public interface Lint<T> {

    /**
     * Name of the lint.
     * @return Lint name
     */
    String name();

    /**
     * Find and return defects.
     * @param entity The entity to analyze (could be {@link XML}
     *  or {@link java.nio.file.Path})
     * @return Defects
     */
    Collection<Defect> defects(T entity) throws IOException;

    /**
     * Returns motive for a lint, explaining why this lint exists.
     * @return Motive text about lint
     * @throws IOException if something went wrong
     */
    String motive() throws IOException;

    /**
     * Lint that always returns a given defect.
     *
     * @since 0.0.35
     */
    final class Mono implements Lint<XML> {
        /**
         * The defect to return.
         */
        private final Defect defect;

        /**
         * Ctor.
         * @param dft The defect to return
         */
        public Mono(final Defect dft) {
            this.defect = dft;
        }

        @Override
        public String name() {
            return this.defect.rule();
        }

        @Override
        public Collection<Defect> defects(final XML xmir) {
            return Collections.singleton(this.defect);
        }

        @Override
        public String motive() {
            throw new UnsupportedOperationException("#motive()");
        }
    }
}
