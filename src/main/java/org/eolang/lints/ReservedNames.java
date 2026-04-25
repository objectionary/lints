/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.yegor256.tojos.MnCsv;
import com.yegor256.tojos.TjCached;
import com.yegor256.tojos.TjDefault;
import com.yegor256.tojos.TjSynchronized;
import org.cactoos.iterable.IterableOf;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapEnvelope;
import org.cactoos.map.MapOf;

/**
 * Reserved EO top object names.
 * @since 0.0.49
 */
final class ReservedNames extends MapEnvelope<String, String> {

    /**
     * Empty ctor.
     */
    ReservedNames() {
        this("target/classes/reserved.csv");
    }

    /**
     * Ctor.
     * @param path Path to reserved names
     */
    ReservedNames(final String path) {
        super(
            new MapOf<>(
                tojo -> new MapEntry<>(tojo.toString(), tojo.get("path")),
                new IterableOf<>(
                    () -> new TjCached(
                        new TjSynchronized(
                            new TjDefault(new MnCsv(path))
                        )
                    ).select(tojo -> true).iterator()
                )
            )
        );
    }
}
