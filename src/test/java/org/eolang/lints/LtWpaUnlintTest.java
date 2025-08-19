/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.cactoos.map.MapOf;
import org.eolang.parser.EoSyntax;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link LtWpaUnlint}.
 *
 * @since 0.0.57
 */
final class LtWpaUnlintTest {

    @Test
    void throwsWhenDefectIsOutsideOfTheScope() {
        MatcherAssert.assertThat(
            "Exception should be thrown, but it was not",
            Assertions.assertThrows(
                Exception.class,
                () -> new LtWpaUnlint(new LtWpaUnlintTest.LtWpaAlways()).defects(
                    new MapOf<>(
                        "x",
                        new EoSyntax(
                            "[] > x"
                        ).parsed()
                    )
                )
            ).getMessage(),
            Matchers.containsString(
                "defect was found in \"stdin\", but this source is not in scope"
            )
        );
    }

    @Test
    void returnsDefectsWithoutUnlints() throws IOException {
        MatcherAssert.assertThat(
            "Returned defect does not match with expected",
            new LtWpaUnlint(new LtInconsistentArgs()).defects(
                new MapOf<>(
                    "foo",
                    new EoSyntax("[] > foo\n  x 1 > x1\n  x 1 2 > x2").parsed()
                )
            ),
            Matchers.allOf(
                Matchers.hasSize(2),
                Matchers.hasToString(
                    Matchers.containsString(
                        "[foo inconsistent-args WARNING]"
                    )
                )
            )
        );
    }

    /**
     * Lint that always complains in WPA scope.
     * @since 0.0.57
     */
    static final class LtWpaAlways implements Lint<Map<String, XML>> {
        @Override
        public String name() {
            return "noname";
        }

        @Override
        public Collection<Defect> defects(final Map<String, XML> entity) {
            return Collections.singletonList(
                new Defect.Default(
                    "some",
                    Severity.ERROR,
                    "stdin",
                    42,
                    "Foo lint fails!"
                )
            );
        }

        @Override
        public String motive() {
            return "nothing";
        }
    }
}
