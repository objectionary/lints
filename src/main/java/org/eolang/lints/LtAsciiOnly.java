/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.github.lombrozo.xnav.Xnav;
import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.IoCheckedText;
import org.cactoos.text.TextOf;
import org.eolang.parser.ObjectName;

/**
 * A comment must include only ASCII characters.
 *
 * @since 0.1.0
 * @todo #14:35min Calculate comment line number with abusive character.
 *  For now we just reusing object line number (via @line), which is not correct
 *  for specifying on which line of the program comment is located. This issue
 *  can be solved after <a href="https://github.com/objectionary/eo/issues/3536">this one</a>.
 * @todo #19:45min Create Lint envelope called `JavaLint` that will fetch motive from
 *  Markdown file based on the lint's name (Java class name) and lint's dimension
 *  (Java package name, e.g. `comments`).
 * @checkstyle StringLiteralsConcatenationCheck (30 lines)
 */
final class LtAsciiOnly implements Lint<XML> {

    @Override
    public Collection<Defect> defects(final XML xmir) throws IOException {
        final Collection<Defect> defects = new LinkedList<>();
        final Xnav xml = new Xnav(xmir.inner());
        final List<Xnav> comments = xml.path("/object/comments/comment")
            .collect(Collectors.toList());
        for (final Xnav comment : comments) {
            final Optional<Character> abusive = comment.text().get().chars()
                .filter(chr -> chr < 32 || chr > 127)
                .mapToObj(chr -> (char) chr)
                .findFirst();
            if (!abusive.isPresent()) {
                continue;
            }
            final String line = comment.attribute("line").text().orElse("0");
            final Character chr = abusive.get();
            defects.add(
                new Defect.Default(
                    "ascii-only",
                    Severity.ERROR,
                    new ObjectName(xmir).get(),
                    Integer.parseInt(line),
                    String.format(
                        "Only ASCII characters are allowed in comments, while \"%s\" is used at the line no.%s at the position no.%s",
                        chr,
                        line,
                        comment.text().get().indexOf(chr) + 1
                    )
                )
            );
        }
        return defects;
    }

    @Override
    public String name() {
        return "ascii-only";
    }

    @Override
    public String motive() throws IOException {
        return new IoCheckedText(
            new TextOf(
                new ResourceOf("org/eolang/motives/comments/ascii-only.md")
            )
        ).asString();
    }
}
