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
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A comment must include only ASCII characters.
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
            final Optional<Character> abusive = comment.text().get().chars()
                .filter(chr -> (chr < 32 && chr != '\n') || chr > 127)
                .mapToObj(chr -> (char) chr)
                .findFirst();
            if (!abusive.isPresent()) {
                continue;
            }
            final Character chr = abusive.get();
            final int pos = comment.text().get().indexOf(chr);
            final int line = new LineOf(comment).value()
                - LtAsciiOnly.shifted(comment.text().get(), pos);
            defects.add(
                new Defect.Default(
                    "ascii-only",
                    Severity.WARNING,
                    line,
                    String.format(
                        "Only ASCII characters are allowed in comments, while \"%s\" is used at the line no.%s at the position no.%s",
                        chr,
                        line,
                        pos + 1
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

    /**
     * Number of line shifts from the given position to the end of the comment.
     * @param text Comment text
     * @param pos Position of the abusive character
     * @return How many newlines follow the position
     */
    private static int shifted(final String text, final int pos) {
        return (int) text.substring(pos).chars().filter(chr -> chr == '\n').count();
    }
}
