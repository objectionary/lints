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
package org.eolang.lints.critical;

import com.jcabi.log.Logger;
import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;
import org.eolang.lints.Defect;
import org.eolang.lints.Lint;
import org.eolang.lints.Severity;

/**
 * Checks that `+alias` is pointing to existing `.xmir` files.
 * @since 0.0.30
 */
public final class LtIncorrectAlias implements Lint<Map<String, XML>> {

    @Override
    public String name() {
        return "incorrect-alias";
    }

    @Override
    public Collection<Defect> defects(final Map<String, XML> pkg) {
        final Collection<Defect> defects = new LinkedList<>();
        pkg.values().forEach(
            xmir -> {
                for (final XML alias : xmir.nodes("/program/metas/meta[head='alias']/tail")) {
                    if (xmir.nodes("/program/metas/meta[head='package']").size() != 1) {
                        continue;
                    }
                    final String pointer = alias.xpath("text()").get(0);
                    final String lookup;
                    if (Boolean.parseBoolean(alias.xpath("contains(text(), ' ')").get(0))) {
                        lookup = alias.xpath("substring-after(text(), ' ')").get(0);
                    } else {
                        lookup = String.format(
                            "%s.%s",
                            xmir.xpath("/program/metas/meta[head='package']/tail/text()").get(0),
                            pointer
                        );
                    }
                    if (!pkg.containsKey(lookup)) {
                        defects.add(
                            new Defect.Default(
                                "incorrect-alias",
                                Severity.CRITICAL,
                                xmir.xpath("/program/@name").stream().findFirst().orElse("unknown"),
                                Integer.parseInt(
                                    xmir.xpath("/program/metas/meta[head='alias'][1]/@line").get(0)
                                ),
                                Logger.format(
                                    "Alias \"%s\" points to \"%s\", but it's not in scope (%d): %[list]s",
                                    pointer, lookup, pkg.size(), pkg.keySet()
                                )
                            )
                        );
                    }
                }
            }
        );
        return defects;
    }

    @Override
    public String motive() throws IOException {
        return new UncheckedText(
            new TextOf(
                new ResourceOf(
                    "org/eolang/motives/critical/incorrect-alias.md"
                )
            )
        ).asString();
    }
}
