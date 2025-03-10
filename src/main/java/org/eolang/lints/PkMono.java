/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
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
            new Mapped<>(
                LtDfSticky::new,
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
            )
        );
    }
}
