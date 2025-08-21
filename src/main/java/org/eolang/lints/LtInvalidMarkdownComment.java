/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.github.lombrozo.xnav.Xnav;
import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.IoCheckedText;
import org.cactoos.text.TextOf;
import org.eolang.parser.OnDefault;

/**
 * Comments should not be violated from the point of view of markdownlint.
 * This lint works for multiple files, as creating a MarkdownLinter takes a very long time
 * and creating it many times is a bad idea.
 *
 * @since 0.0.47
 */
final class LtInvalidMarkdownComment implements Lint<Map<String, XML>> {
    /**
     * Markdownlint rule names to ignore.
     */
    private static final Set<String> IGNORED = Set.of(
        "MD041",
        "MD047",
        "MD026"
    );

    @Override
    public String name() {
        return "invalid-markdown-comment";
    }

    @Override
    public Collection<Defect> defects(final Map<String, XML> pkg) throws IOException {
        final Collection<Defect> defects = new ArrayList<>(0);
        try (MarkdownLinter mdlinter = new MarkdownLinter()) {
            for (final XML xmir : pkg.values()) {
                this.defectsOfXmir(xmir, mdlinter).forEach(defects::add);
            }
        }
        return defects;
    }

    @Override
    public String motive() throws IOException {
        return new IoCheckedText(
            new TextOf(
                new ResourceOf(
                    String.format(
                        "org/eolang/motives/comments/%s.md",
                        this.name()
                    )
                )
            )
        ).asString();
    }

    private Stream<Defect> defectsOfXmir(final XML xmir, final MarkdownLinter mdlinter) {
        final Stream.Builder<Defect> defects = Stream.builder();
        final List<Xnav> comments = new Xnav(xmir.inner()).path("/object/comments/comment")
            .collect(Collectors.toList());
        for (final Xnav comment : comments) {
            final String text = comment.text().get().replace("\\n", "\n");
            final int length = text.split("\n", -1).length;
            final int line = Integer.parseInt(comment.attribute("line").text().orElse("0"));
            mdlinter.defects(text)
                .filter(defect -> !LtInvalidMarkdownComment.IGNORED.contains(defect.rule()))
                .map(
                    defect -> new Defect.Default(
                        this.name(),
                        defect.severity(),
                        new OnDefault(xmir).get(),
                        line - 1 - length + defect.line(),
                        defect.text()
                    )
                )
                .forEach(defects::add);
        }
        return defects.build();
    }
}
