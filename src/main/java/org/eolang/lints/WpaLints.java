/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import java.util.Map;
import org.cactoos.iterable.IterableEnvelope;
import org.cactoos.list.ListOf;

/**
 * WPA lints.
 *
 * @since 0.0.43
 */
final class WpaLints extends IterableEnvelope<Lint<Map<String, XML>>> {

    /**
     * Ctor.
     */
    WpaLints() {
        super(
            new ListOf<>(
                new LtUnitTestMissing(),
                new LtUnitTestWithoutLiveFile(),
                new LtIncorrectAlias(),
                new LtObjectIsNotUnique(),
                new LtAtomIsNotUnique(),
                new LtInconsistentArgs(),
                new LtIncorrectNumberOfAttrs()
            )
        );
    }
}
