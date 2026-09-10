/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The {@code @name} of a unit test attribute, as the EO parser spells it.
 *
 * <p>The parser marks a truthy test attribute with the {@code p🌵} prefix
 * and a throwing one with {@code n🌵}. Older parsers used {@code +} and
 * {@code -} for the same purpose, which are not legal φ-calculus attribute
 * names. Both spellings are accepted, so that {@code eo:lints} and
 * {@code eo} may move independently.</p>
 *
 * <p>This is the Java twin of {@code /org/eolang/funcs/test-name.xsl}, for
 * the lints that are not XSL stylesheets. Keep the two in sync.</p>
 *
 * @since 0.2.12
 */
final class MarkedName {

    /**
     * Markers of a truthy test attribute, which asserts that something works.
     */
    private static final Collection<String> POSITIVE = Collections.unmodifiableList(
        Arrays.asList("p🌵", "+")
    );

    /**
     * Markers of a throwing test attribute, which asserts that something fails.
     */
    private static final Collection<String> NEGATIVE = Collections.unmodifiableList(
        Arrays.asList("n🌵", "-")
    );

    /**
     * The name, with the marker still attached to it.
     */
    private final String origin;

    /**
     * Ctor.
     * @param name The name of the attribute, with the marker
     */
    MarkedName(final String name) {
        this.origin = name;
    }

    /**
     * The name with its marker removed, e.g. {@code can-do-it} for
     * {@code p🌵can-do-it}. A name that is not a test comes back untouched.
     * @return The name, without the marker
     */
    String title() {
        String title = this.origin;
        for (final String marker : MarkedName.markers()) {
            if (this.origin.startsWith(marker)) {
                title = this.origin.substring(marker.length());
                break;
            }
        }
        return title;
    }

    /**
     * XPath that selects every truthy test attribute of an XMIR document.
     * @return XPath expression
     */
    static String positives() {
        return MarkedName.POSITIVE.stream()
            .map(marker -> String.format("starts-with(@name, '%s')", marker))
            .collect(Collectors.joining(" or ", "/object//o[@name and (", ")]"));
    }

    private static Collection<String> markers() {
        return Stream.concat(
            MarkedName.POSITIVE.stream(), MarkedName.NEGATIVE.stream()
        ).collect(Collectors.toList());
    }
}
