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
import java.util.Arrays;
import javax.annotation.concurrent.ThreadSafe;
import org.cactoos.iterable.IterableEnvelope;
import org.cactoos.iterable.Joined;
import org.cactoos.iterable.Shuffled;
import org.eolang.lints.comments.LtAsciiOnly;
import org.eolang.lints.misc.LtTestNotVerb;

/**
 * Collection of lints for individual XML files, provided
 * by the {@link Program} class.
 *
 * <p>This class is thread-safe.</p>
 *
 * @since 0.23
 */
@ThreadSafe
final class PkMono extends IterableEnvelope<Lint<XML>> {

    /**
     * Default ctor.
     */
    PkMono() {
        super(
            new Shuffled<>(
                new Joined<Lint<XML>>(
                    new PkByXsl(),
                    Arrays.asList(
                        new LtAsciiOnly(),
                        new LtTestNotVerb()
                    )
                )
            )
        );
    }
}
