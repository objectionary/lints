/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The "Incorrect:"/"Correct:" examples encoded in a lint's motive text.
 *
 * <p>Only fenced "eo" and "xml" blocks that immediately follow (possibly
 * several in a row) an "Incorrect:" or "Correct:" label are considered
 * examples; once a paragraph of prose follows a block, the section is
 * closed and further blocks are ignored, since they may illustrate a case
 * the label no longer describes.</p>
 *
 * @since 0.0.1
 */
final class Motives implements Iterable<Example> {

    /**
     * The motive text, as returned by {@link Lint#motive()}.
     */
    private final String text;

    /**
     * Ctor.
     *
     * @param txt Motive text
     */
    Motives(final String txt) {
        this.text = txt;
    }

    @Override
    public Iterator<Example> iterator() {
        return this.examples().iterator();
    }

    private Collection<Example> examples() {
        final List<String> lines = this.text.lines().collect(Collectors.toList());
        // @checkstyle ConditionalRegexpMultilineCheck (1 line)
        final Collection<Example> found = new ArrayList<>();
        boolean open = false;
        boolean correct = false;
        int idx = 0;
        while (idx < lines.size()) {
            final String trimmed = lines.get(idx).trim();
            if ("Incorrect:".equals(trimmed)) {
                open = true;
                correct = false;
                idx += 1;
            } else if ("Correct:".equals(trimmed)) {
                open = true;
                correct = true;
                idx += 1;
            } else if (trimmed.startsWith("```")) {
                final int end = Motives.fenceEnd(lines, idx + 1);
                final String lang = trimmed.substring(3).trim();
                if (open && ("eo".equals(lang) || "xml".equals(lang))) {
                    found.add(
                        new Example(lang, Motives.fenceBody(lines, idx + 1, end), correct)
                    );
                }
                idx = end + 1;
                open = open && Motives.followedByFence(lines, idx);
            } else {
                idx += 1;
            }
        }
        return found;
    }

    private static int fenceEnd(final List<String> lines, final int start) {
        int idx = start;
        while (idx < lines.size() && !"```".equals(lines.get(idx).trim())) {
            idx += 1;
        }
        return idx;
    }

    private static String fenceBody(final List<String> lines, final int start, final int end) {
        final StringBuilder body = new StringBuilder();
        for (int idx = start; idx < end; idx += 1) {
            body.append(lines.get(idx)).append('\n');
        }
        return body.toString();
    }

    private static boolean followedByFence(final List<String> lines, final int from) {
        int peek = from;
        while (peek < lines.size() && lines.get(peek).isBlank()) {
            peek += 1;
        }
        return peek < lines.size() && lines.get(peek).trim().startsWith("```");
    }
}
