/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Tests for {@link VoidXpath}.
 *
 * @since 0.0.50
 */
final class VoidXpathTest {

    @ParameterizedTest
    @CsvSource(
        {
            "main.ξ.x.∅, //o[@name='main']/o[@base='ξ.x']",
            "foo.foo.foo.ξ.x.∅, //o[@name='foo']/o[@name='foo']/o[@name='foo']/o[@base='ξ.x']",
            "@.foo.:anonymous.ξ.x.∅, //o[@name='@']/o[@name='foo']/o/o[@base='ξ.x']"
        }
    )
    void convertsToXpath(final String fqn, final String xpath) {
        MatcherAssert.assertThat(
            String.format(
                "Generated XPath for FQN: '%s' does not match with expected format",
                fqn
            ),
            new VoidXpath(fqn).asString(),
            Matchers.equalTo(xpath)
        );
    }
}
