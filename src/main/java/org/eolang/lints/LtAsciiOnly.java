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
 * @todo #14:35min Calculate comment line number with abusive character.
 *  For now we just reusing object line number (via @line), which is not correct
 *  for specifying on which line of the program comment is located. This issue
 *  can be solved after <a href="https://github.com/objectionary/eo/issues/3536">this one</a>.
 * @todo #402:15min Replace the creation of new ArrayList<>(0) with the creation of
 *  ArrayList<>() without a constructor argument in whole project. Add ignore warning
 *  ConditionalRegexpMultilineCheck from Checkstyle (it doesn't seem to be possible at the moment
 *  <a href="https://github.com/yegor256/qulice/issues/1328">issue 1328</a>)
 * @checkstyle StringLiteralsConcatenationCheck (30 lines)
 */
final class LtAsciiOnly implements Lint {

    @Override
    public Collection<Defect> defects(final XML xmir) throws IOException {
        final Collection<Defect> defects = new ArrayList<>(0);
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
            final int line = new LineOf(comment).value();
            final Character chr = abusive.get();
            defects.add(
                new Defect.Default(
                    "ascii-only",
                    Severity.WARNING,
                    line,
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
        return new MotiveFrom("comments", this.name()).asString();
    }

    @Override
    public Fix fix() {
        return new FxEmpty();
    }
}
