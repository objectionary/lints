/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import javax.annotation.concurrent.ThreadSafe;
import org.cactoos.func.Chained;
import org.cactoos.iterable.IterableEnvelope;
import org.cactoos.iterable.Joined;
import org.cactoos.iterable.Mapped;
import org.cactoos.list.ListOf;

/**
 * Collection of lints for individual XML files, provided
 * by the {@link Source} class.
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
    private static final Iterable<Lint<XML>> LINTS = new MonoLints();

    /**
     * Default ctor.
     */
    PkMono() {
        this(PkMono.LINTS);
    }

    /**
     * Ctor.
     * @param lints Lints
     */
    PkMono(final Iterable<Lint<XML>> lints) {
        super(
            new Joined<>(
                new Mapped<Lint<XML>>(
                    new Chained<>(LtUnlint::new, LtDfSticky::new),
                    new Joined<Lint<XML>>(
                        lints,
                        new ListOf<>(
                            new LtUnlintNonExistingDefect(
                                lints, new ListOf<>(new WpaLintNames())
                            )
                        )
                    )
                )
            )
        );
    }
}
