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
package org.eolang.lints.errors;

import com.jcabi.xml.XML;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.TextOf;
import org.eolang.lints.Defect;
import org.eolang.lints.Lint;
import org.eolang.lints.Severity;

/**
 * Object is not unique.
 *
 * @since 0.0.30
 */
public final class LtObjectIsNotUnique implements Lint<Map<String, XML>> {

    @Override
    public String name() {
        return "object-is-not-unique";
    }

    @Override
    public Collection<Defect> defects(final Map<String, XML> pkg) {
        final Collection<Defect> defects = new LinkedList<>();
        for (final XML xmir : pkg.values()) {
            final String src = xmir.xpath("/program/@name").stream().findFirst().orElse("unknown");
            if (xmir.nodes("/program/objects/o").isEmpty()) {
                continue;
            }
            for (final XML oth : pkg.values()) {
                if (Objects.equals(oth, xmir)) {
                    continue;
                }
                if (oth.nodes("/program/objects/o").isEmpty()) {
                    continue;
                }
                if (!LtObjectIsNotUnique.shareSameName(xmir, oth)) {
                    continue;
                }
                defects.add(
                    new Defect.Default(
                        this.name(),
                        Severity.ERROR,
                        oth.xpath("/program/@name").stream()
                            .findFirst()
                            .orElse("unknown"),
                        Integer.parseInt(
                            oth.xpath("/program/objects/o[1]/@line").get(0)
                        ),
                        String.format(
                            "The object name '%s' is not unique, original object was found in '%s'",
                            xmir.xpath("/program/objects/o[1]/@name"), src
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
                String.format(
                    "org/eolang/motives/errors/%s.md", this.name()
                )
            )
        ).asString();
    }

    private static boolean shareSameName(final XML original, final XML oth) {
        return original.xpath("/program/objects/o[1]/@name").get(0)
            .equals(oth.xpath("/program/objects/o[1]/@name").get(0))
            && LtObjectIsNotUnique.packageName(original).equals(
            LtObjectIsNotUnique.packageName(oth)
        );
    }

    private static String packageName(final XML xmir) {
        final String name;
        if (xmir.nodes("/program/metas/meta[head='package']").size() == 1) {
            name = xmir.xpath("/program/metas/meta[head='package']/tail/text()").get(0);
        } else {
            name = "";
        }
        return name;
    }
}