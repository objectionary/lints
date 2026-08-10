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
        final Optional<String> listing = xml.path("//listing")
            .findFirst().map(elem -> elem.text().get());
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
            final String text = comment.text().get();
            final int pos = text.indexOf(chr);
            final int line;
            if (listing.isPresent()) {
                final Optional<Integer> found = LtAsciiOnly.locate(
                    listing.get(), text
                );
                if (found.isPresent()) {
                    line = found.get() + (int) text.substring(0, pos).chars()
                        .filter(c -> c == '\n').count();
                } else {
                    line = new LineOf(comment).value();
                }
            } else {
                line = new LineOf(comment).value();
            }
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
     * Real source line of the abusive character.
     * The comment text is located in the program listing, where each line
     * starts with the {@code #} sign. The line of the character is the line
     * of the first line of the comment.
     * @param listing Full program listing
     * @param text Comment text
     * @return Real source line of the first line of the comment, if found
     */
    private static Optional<Integer> locate(final String listing, final String text) {
        final int newline = text.indexOf('\n');
        final String headline;
        if (newline < 0) {
            headline = text;
        } else {
            headline = text.substring(0, newline);
        }
        final int index = listing.indexOf("# ".concat(headline));
        final Optional<Integer> result;
        if (index < 0) {
            result = Optional.empty();
        } else {
            result = Optional.of(
                (int) listing.substring(0, index).chars()
                    .filter(chr -> chr == '\n').count() + 1
            );
        }
        return result;
    }
}
