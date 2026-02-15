/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import java.io.IOException;
import org.eolang.parser.EoSyntax;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link LtSyntaxVersion}.
 *
 * @since 0.1.0
 */
final class LtSyntaxVersionTest {

    @Test
    void catchesMismatchWhenSyntaxIsNewer() throws IOException {
        MatcherAssert.assertThat(
            "should report error when +syntax version is newer than parser",
            new LtSyntaxVersion("0.58.0").defects(
                new EoSyntax(
                    "+syntax 0.59.0\n\n# Foo.\n[] > foo\n"
                ).parsed()
            ),
            Matchers.<Defect>iterableWithSize(1)
        );
    }

    @Test
    void allowsWhenParserIsNewer() throws IOException {
        MatcherAssert.assertThat(
            "should not report error when parser version is newer",
            new LtSyntaxVersion("0.60.0").defects(
                new EoSyntax(
                    "+syntax 0.59.0\n\n# Foo.\n[] > foo\n"
                ).parsed()
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void allowsWhenVersionsMatch() throws IOException {
        MatcherAssert.assertThat(
            "should not report error when versions match exactly",
            new LtSyntaxVersion("0.59.0").defects(
                new EoSyntax(
                    "+syntax 0.59.0\n\n# Foo.\n[] > foo\n"
                ).parsed()
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void usesDefaultVersionWhenNoArgCtor() throws IOException {
        MatcherAssert.assertThat(
            "with default ctor (0.0.0), +syntax 0.0.0 should not report error",
            new LtSyntaxVersion().defects(
                new EoSyntax(
                    "+syntax 0.0.0\n\n# Foo.\n[] > foo\n"
                ).parsed()
            ),
            Matchers.emptyIterable()
        );
        MatcherAssert.assertThat(
            "with default ctor (0.0.0), +syntax 0.0.1 should report error",
            new LtSyntaxVersion().defects(
                new EoSyntax(
                    "+syntax 0.0.1\n\n# Foo.\n[] > foo\n"
                ).parsed()
            ),
            Matchers.<Defect>iterableWithSize(1)
        );
    }

    @Test
    void ignoresWhenNoSyntaxMeta() throws IOException {
        MatcherAssert.assertThat(
            "should not report error when no +syntax meta is present",
            new LtSyntaxVersion("0.59.0").defects(
                new EoSyntax(
                    "# Foo.\n[] > foo\n"
                ).parsed()
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void reportsErrorOnInvalidSyntaxFormat() throws IOException {
        MatcherAssert.assertThat(
            "should report error for invalid +syntax format",
            new LtSyntaxVersion("0.59.0").defects(
                new EoSyntax(
                    "+syntax alpha\n\n# Foo.\n[] > foo\n"
                ).parsed()
            ),
            Matchers.<Defect>iterableWithSize(1)
        );
    }

    @Test
    void reportsErrorSeverityForInvalidFormat() throws IOException {
        MatcherAssert.assertThat(
            "invalid format defect should have error severity",
            new LtSyntaxVersion("0.59.0").defects(
                new EoSyntax(
                    "+syntax alpha\n\n# Foo.\n[] > foo\n"
                ).parsed()
            ).iterator().next().severity(),
            Matchers.equalTo(Severity.ERROR)
        );
    }

    @Test
    void reportsErrorSeverity() throws IOException {
        MatcherAssert.assertThat(
            "defect should have error severity",
            new LtSyntaxVersion("0.58.0").defects(
                new EoSyntax(
                    "+syntax 0.59.0\n\n# Foo.\n[] > foo\n"
                ).parsed()
            ).iterator().next().severity(),
            Matchers.equalTo(Severity.ERROR)
        );
    }

    @Test
    void catchesMajorVersionMismatch() throws IOException {
        MatcherAssert.assertThat(
            "should detect when major version is newer",
            new LtSyntaxVersion("0.59.0").defects(
                new EoSyntax(
                    "+syntax 1.0.0\n\n# Foo.\n[] > foo\n"
                ).parsed()
            ),
            Matchers.<Defect>iterableWithSize(1)
        );
    }

    @Test
    void catchesPatchVersionMismatch() throws IOException {
        MatcherAssert.assertThat(
            "should detect when patch version is newer",
            new LtSyntaxVersion("0.59.0").defects(
                new EoSyntax(
                    "+syntax 0.59.1\n\n# Foo.\n[] > foo\n"
                ).parsed()
            ),
            Matchers.<Defect>iterableWithSize(1)
        );
    }
}
