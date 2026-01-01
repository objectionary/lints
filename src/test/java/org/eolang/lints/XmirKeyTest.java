/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.yegor256.Mktmp;
import com.yegor256.MktmpResolver;
import java.nio.file.Path;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Tests for {@link XmirKey}.
 *
 * @since 0.0.30
 */
@ExtendWith(MktmpResolver.class)
final class XmirKeyTest {

    @Test
    void returnsXmirName(@Mktmp final Path dir) {
        MatcherAssert.assertThat(
            "invalid key generated",
            new XmirKey(
                dir.resolve(String.format("%s.xmir", "bar")), dir
            ).asString(),
            Matchers.equalTo("bar")
        );
    }

    @Test
    void returnsXmirNameWithParentDirs(@Mktmp final Path dir) {
        MatcherAssert.assertThat(
            "invalid key generated",
            new XmirKey(
                dir.resolve("ttt").resolve("aaa").resolve("foo.xmir"), dir
            ).asString(),
            Matchers.equalTo("ttt.aaa.foo")
        );
    }
}
