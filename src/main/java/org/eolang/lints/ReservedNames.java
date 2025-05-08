/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
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
import org.cactoos.Scalar;
import org.cactoos.map.MapOf;

/**
 * Reserved EO top object names.
 * @since 0.0.49
 */
final class ReservedNames implements Scalar<Map<String, String>> {

    /**
     * Path to reserved names.
     */
    private final String path;

    /**
     * Empty ctor.
     */
    ReservedNames() {
        this("target/classes/reserved.csv");
    }

    /**
     * Ctor.
     * @param pth Path to reserved names
     */
    ReservedNames(final String pth) {
        this.path = pth;
    }

    @Override
    public Map<String, String> value() {
        final Map<String, String> result = new MapOf<>();
        final List<Tojo> selected = new TjCached(
            new TjSynchronized(
                new TjDefault(
                    new MnCsv(this.path)
                )
            )
        ).select(tojo -> true);
        selected.forEach(tojo -> result.put(tojo.toString(), tojo.get("path")));
        return result;
    }
}
