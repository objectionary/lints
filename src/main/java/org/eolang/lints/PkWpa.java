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
import java.util.Map;
import org.cactoos.iterable.IterableEnvelope;
import org.cactoos.iterable.Mapped;
import org.cactoos.iterable.Shuffled;
import org.cactoos.list.ListOf;

/**
 * A collection of lints for Whole Program Analysis (WPA),
 * provided by the {@link Programs} class.
 *
 * <p>This class is thread-safe.</p>
 *
 * @since 0.1.0
 */
final class PkWpa extends IterableEnvelope<Lint<Map<String, XML>>> {

    /**
     * Ctor.
     */
    PkWpa() {
        super(
            new Shuffled<>(
                new Mapped<>(
                    lnt -> new LtWpaUnlint(lnt),
                    new ListOf<>(
                        new LtUnitTestMissing(),
                        new LtUnitTestWithoutLiveFile(),
                        new LtIncorrectAlias(),
                        new LtObjectIsNotUnique(),
                        new LtAtomIsNotUnique()
                    )
                )
            )
        );
    }
}
