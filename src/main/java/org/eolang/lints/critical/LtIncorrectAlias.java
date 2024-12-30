/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2024 Objectionary.com
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

import com.jcabi.xml.XML;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.TextOf;
import org.eolang.lints.Defect;
import org.eolang.lints.Lint;
import org.eolang.lints.Severity;

/**
 * Checks that `+alias` is pointing to existing `.xmir` files.
 * @since 0.0.30
 */
public final class LtIncorrectAlias implements Lint<XML> {

    /**
     * Base path.
     */
    private final Path base;

    /**
     * Ctor.
     * @param bse Base path
     */
    public LtIncorrectAlias(final Path bse) {
        this.base = bse;
    }

    @Override
    public Collection<Defect> defects(final XML xmir) {
        final Collection<Defect> defects = new LinkedList<>();
        for (final XML alias : xmir.nodes("//meta[head='alias']/tail")) {
            if (!"1".equals(xmir.xpath("count(//meta[head='package'])").get(0))) {
                continue;
            }
            final String pointer = alias.xpath("text()").get(0);
            final Path candidate = Path.of(
                String.valueOf(this.base.toUri().getPath()),
                xmir.xpath("//meta[head='package']/tail/text()").get(0),
                String.format("%s.xmir", pointer)
            );
            if (!Files.exists(candidate)) {
                defects.add(
                    new Defect.Default(
                        "incorrect-alias",
                        Severity.CRITICAL,
                        xmir.xpath("/program/@name").stream().findFirst().orElse("unknown"),
                        Integer.parseInt(xmir.xpath("//meta[head='alias'][1]/@line").get(0)),
                        String.format(
                            "Incorrect pointing alias '%s', there is no %s",
                            pointer,
                            candidate
                        )
                    )
                );
            }
        }
        return defects;
    }

    @Override
    public String motive() throws Exception {
        return new TextOf(
            new ResourceOf(
                "org/eolang/motives/critical/incorrect-alias.md"
            )
        ).asString();
    }
}