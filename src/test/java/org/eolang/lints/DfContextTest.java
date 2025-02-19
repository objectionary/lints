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

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link DfContext}.
 *
 * @since 0.0.40
 */
final class DfContextTest {

    @Test
    void printsContextAsXml() {
        MatcherAssert.assertThat(
            "XML does not match with expected",
            new DfContext(
                new Defect.Default(
                    "foo",
                    Severity.ERROR,
                    "app.main",
                    0,
                    "Boom!"
                ),
                "&lt;o base=&#34;man&#34;&gt;&#xA;&lt;o as=&#34;name&#34; base=&#34;string&#34;/&gt;&#xA;&lt;o base=&#34;number&#34;/&gt;&#xA;&lt;/o&gt;"
            ).context(),
            Matchers.equalTo(
                "<o base=\"man\">\n<o as=\"name\" base=\"string\"/>\n<o base=\"number\"/>\n</o>"
            )
        );
    }
}
