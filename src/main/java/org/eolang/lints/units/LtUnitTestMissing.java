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
import org.eolang.lints.Defect;
import org.eolang.lints.Lint;
import org.eolang.lints.Severity;

/**
 * A test is missing for a live EO program.
 *
 * @since 0.1.0
 */
public final class LtUnitTestMissing implements Lint<Map<String, XML>> {

    @Override
    public Collection<Defect> defects(final Map<String, XML> pkg) throws IOException {
        final Collection<Defect> defects = new LinkedList<>();
        for (final String name : pkg.keySet()) {
            if (name.endsWith("-tests")) {
                continue;
            }
            final String lookup = String.format("%s-tests", name);
            if (pkg.containsKey(lookup)) {
                continue;
            }
            defects.add(
                new Defect.Default(
                    "unit-test-missing",
                    Severity.WARNING,
                    name,
                    0,
                    String.format("Tests \"%s\" not found for \"%s\"", lookup, name)
                )
            );
        }
        return defects;
    }

    @Override
    public String name() {
        return "unit-test-missing";
    }

    @Override
    public String motive() throws IOException {
        return "";
    }
}
