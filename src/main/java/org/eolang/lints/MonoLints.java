/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import java.util.List;
import java.util.stream.Collectors;
import org.cactoos.iterable.IterableEnvelope;
import org.cactoos.iterable.Joined;
import org.cactoos.iterable.Shuffled;
import org.cactoos.list.ListOf;

/**
 * Mono lints.
 * @since 0.0.43
 */
final class MonoLints extends IterableEnvelope<Lint<XML>> {

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
     * Ctor.
     */
    MonoLints() {
        super(
            new Joined<Lint<XML>>(
                MonoLints.LINTS,
                List.of(
                    new LtIncorrectUnlint(
                        new ListOf<>(
                            new Joined<>(
                                MonoLints.LINTS, new WpaLints(),
                                List.of(
                                    new LtUnlintNonExistingDefect(
                                        MonoLints.LINTS, new ListOf<>(new WpaLintNames())
                                    )
                                )
                            )
                        ).stream()
                            .map(Lint::name)
                            .collect(Collectors.toList())
                    )
                )
            )
        );
    }
}
