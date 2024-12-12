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
package org.eolang.lints.comments;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;

import com.jcabi.xml.XML;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.TextOf;
import org.eolang.lints.Defect;
import org.eolang.lints.Lint;
import org.eolang.lints.Severity;

/**
 * A comment must include only ASCII characters.
 *
 * @since 0.1.0
 * @todo #14:35min Calculate comment line number with abusive character.
 *  For now we just reusing object line number (via @line), which is not correct
 *  for specifying on which line of the program comment is located. This issue
 *  can be solved after <a href="https://github.com/objectionary/eo/issues/3536">this one</a>.
 * @checkstyle StringLiteralsConcatenationCheck (30 lines)
 */
public final class AsciiOnly implements Lint<XML> {

    @Override
    public Collection<Defect> defects(final XML xmir) throws IOException {
        final Collection<Defect> defects = new LinkedList<>();
        for (final XML comment : xmir.nodes("/program/comments/comment")) {
            final Optional<Character> abusive = comment.xpath("text()").get(0).chars()
                .filter(chr -> chr < 32 || chr > 127)
                .mapToObj(chr -> (char) chr)
                .findFirst();
            if (!abusive.isPresent()) {
                continue;
            }
            final String line = comment.xpath("@line").get(0);
            final Character chr = abusive.get();
            defects.add(
                new Defect.Default(
                    "ascii-only",
                    Severity.ERROR,
                    xmir.xpath("/program/@name").stream().findFirst().orElse("unknown"),
                    Integer.parseInt(line),
                    String.format(
                        "Only ASCII characters are allowed in comments, while '%s' is used at the %sth line at the %sth position",
                        chr,
                        line,
                        comment.xpath("text()").get(0).indexOf(chr) + 1
                    )
                )
            );
        }
        return defects;
    }

    @Override
    public String motive() throws Exception {
        return new TextOf(
            new ResourceOf("org/eolang/motives/comments/ascii-only.md")
        ).asString();
    }
}
