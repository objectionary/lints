/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import java.util.Map;
import org.cactoos.iterable.IterableEnvelope;
import org.cactoos.iterable.Joined;
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
     * WPA lints.
     */
    private static final Iterable<Lint<Map<String, XML>>> WPA = new WpaLints();

    /**
     * Default ctor.
     */
    PkWpa() {
        this(PkWpa.WPA);
    }

    /**
     * Ctor.
     * @param lints Lints
     */
    PkWpa(final Iterable<Lint<Map<String, XML>>> lints) {
        super(
            new Shuffled<>(
                new Mapped<Lint<Map<String, XML>>>(
                    LtWpaUnlint::new,
                    new Joined<Lint<Map<String, XML>>>(
                        lints,
                        new ListOf<>(
                            new LtUnlintNonExistingDefectWpa(
                                lints, new ListOf<>(new MonoLintNames())
                            )
                        )
                    )
                )
            )
        );
    }
}
