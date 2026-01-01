/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.yegor256.tojos.MnCsv;
import com.yegor256.tojos.TjCached;
import com.yegor256.tojos.TjDefault;
import com.yegor256.tojos.TjSynchronized;
import com.yegor256.tojos.Tojo;
import java.util.List;
import java.util.Map;
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
        super(ReservedNames.names(path));
    }

    /**
     * Names.
     * @param path Path to CSV file
     * @return Map of object names, key is name, value is path
     */
    private static Map<String, String> names(final String path) {
        final Map<String, String> result = new MapOf<>();
        final List<Tojo> selected = new TjCached(
            new TjSynchronized(
                new TjDefault(
                    new MnCsv(path)
                )
            )
        ).select(tojo -> true);
        selected.forEach(tojo -> result.put(tojo.toString(), tojo.get("path")));
        return result;
    }
}
