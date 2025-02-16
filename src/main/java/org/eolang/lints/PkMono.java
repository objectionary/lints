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
import java.util.List;
import javax.annotation.concurrent.ThreadSafe;
import org.cactoos.iterable.IterableEnvelope;
import org.cactoos.iterable.Joined;
import org.cactoos.iterable.Mapped;
import org.cactoos.iterable.Shuffled;

/**
 * Collection of lints for individual XML files, provided
 * by the {@link Program} class.
 *
 * <p>This class is thread-safe.</p>
 *
 * @since 0.23
 * @todo #297:35min Return `LtTestNotVerb` back.
 *  For some reason this lint produces errors in EO-to-Java Compiler. Check
 *  <a href="https://github.com/objectionary/lints/issues/297#issuecomment-2636540673">this</a>
 *  issue for more details. We should return it in the fixed state, once we understand
 *  the root cause of the problem.
 */
@ThreadSafe
final class PkMono extends IterableEnvelope<Lint<XML>> {

    /**
     * All XML-based lints.
     */
    private static final Iterable<Lint<XML>> LINTS = new Shuffled<>(
        new Joined<Lint<XML>>(
            new PkByXsl(),
            List.of(
                new LtAsciiOnly()
            )
        )
    );

    /**
     * Default ctor.
     */
    PkMono() {
        super(
            new Joined<Lint<XML>>(
                new Mapped<Lint<XML>>(
                    LtUnlint::new,
                    new Joined<Lint<XML>>(
                        PkMono.LINTS,
                        List.of(new LtUnlintNonExistingDefect(PkMono.LINTS))
                    )
                ),
                List.of(
                    new LtIncorrectUnlint(
                        new Mapped<>(
                            Lint::name,
                            new Joined<>(
                                new PkWpa(),
                                PkMono.LINTS,
                                List.of(new LtUnlintNonExistingDefect(PkMono.LINTS))
                            )
                        )
                    )
                )
            )
        );
    }
}
