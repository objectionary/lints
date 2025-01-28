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
package org.eolang.lints.units;

import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.IoCheckedText;
import org.cactoos.text.TextOf;
import org.eolang.lints.Defect;
import org.eolang.lints.Lint;
import org.eolang.lints.Severity;

/**
 * Unit test without live file.
 *
 * @since 0.0.30
 */
public final class LtUnitTestWithoutLiveFile implements Lint<Map<String, XML>> {

    @Override
    public String name() {
        return "unit-test-without-live-file";
    }

    @Override
    public Collection<Defect> defects(final Map<String, XML> pkg) {
        final Collection<Defect> defects = new LinkedList<>();
        for (final String name : pkg.keySet()) {
            if (!name.endsWith("-test")) {
                continue;
            }
            final String live = name.replace("-test", "");
            if (pkg.containsKey(live)) {
                continue;
            }
            defects.add(
                new Defect.Default(
                    this.name(),
                    Severity.WARNING,
                    name,
                    0,
                    String.format(
                        "Live \".eo\" file \"%s\" was not found for \"%s\"", live, name
                    )
                )
            );
        }
        return defects;
    }

    @Override
    public String motive() throws IOException {
        return new IoCheckedText(
            new TextOf(
                new ResourceOf(
                    String.format(
                        "org/eolang/motives/units/%s.md", this.name()
                    )
                )
            )
        ).asString();
    }
}
