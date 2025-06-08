/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
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
            "main.$.x.∅, //o[@name='main']/o[@base='$.x']",
            "foo.foo.foo.$.x.∅, //o[@name='foo']/o[@name='foo']/o[@name='foo']/o[@base='$.x']",
            "@.foo.:anonymous.$.x.∅, //o[@name='@']/o[@name='foo']/o/o[@base='$.x']",
            // todo: why `@` and `:anonymous` are reverted in the path?
            // handle ":anonymous.@.@.:anonymous.@.foo.:anonymous.$.x.∅
            // should be: foo.@.o.o.$.x.∅
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
