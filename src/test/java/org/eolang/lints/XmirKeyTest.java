/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2025 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
