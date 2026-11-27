/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.github.lombrozo.xnav.Xnav;
import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;

/**
 * A comment must include only ASCII characters.
 *
 * <p>The parser puts a comment block into a single {@code <comment/>}, with
 * one line of text per line of the source, and its {@code @line} pointing to
 * the last of them. The defect is thus reported at the very line of the
 * source where the first abusive character is, and its position is counted
 * from the start of that line of the comment.</p>
 *
 * @since 0.1.0
 * @checkstyle UnnecessaryParenthesesCheck (30 lines)
 */
final class LtAsciiOnly implements Lint {

    @Override
    public Collection<Defect> defects(final XML xmir) throws IOException {
        // @checkstyle ConditionalRegexpMultilineCheck (1 line)
        final Collection<Defect> defects = new ArrayList<>();
        final Xnav xml = new Xnav(xmir.inner());
        final List<Xnav> comments = xml.path("/object/comments/comment")
            .collect(Collectors.toList());
        for (final Xnav comment : comments) {
            final String text = comment.text().orElse("");
            if (text.isEmpty()) {
                continue;
            }
            final OptionalInt abusive = text.codePoints()
                .filter(chr -> (chr < 32 && chr != '\n') || chr > 127)
                .findFirst();
            if (!abusive.isPresent()) {
                continue;
            }
            final String chr = String.valueOf(Character.toChars(abusive.getAsInt()));
            final String[] lines = text.split(String.valueOf('\n'), -1);
            int idx = 0;
            while (!lines[idx].contains(chr)) {
                idx += 1;
            }
            final int line = LtAsciiOnly.lineOf(new LineOf(comment).value(), lines.length, idx);
            defects.add(
                new Defect.Default(
                    "ascii-only",
                    Severity.WARNING,
                    line,
                    String.format(
                        "Only ASCII characters are allowed in comments, while \"%s\" is used at the line no.%s at the position no.%s",
                        chr,
                        line,
                        lines[idx].indexOf(chr) + 1
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
        return new MotiveFrom("comments", this.name()).asString();
    }

    @Override
    public Fix fix() {
        return new FxEmpty();
    }

    private static int lineOf(final int last, final int total, final int idx) {
        final int first = last - total + 1;
        final int line;
        if (last > 0 && first > 0) {
            line = first + idx;
        } else {
            line = last;
        }
        return line;
    }
}
