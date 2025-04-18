/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import java.util.Map;
import org.cactoos.iterable.IterableEnvelope;

/**
 * WPA lints without lint names.
 * @since 0.0.46
 */
final class WpaWithout extends IterableEnvelope<Lint<Map<String, XML>>> {

    /**
     * WPA lints.
     */
    private static final Iterable<Lint<Map<String, XML>>> WPA = new WpaLints();

    /**
     * Ctor.
     *
     * @param names Lints to exclude
     */
    WpaWithout(final String... names) {
        super(new PkWpa(new WithoutLints<>(WpaWithout.WPA, names)));
    }
}
