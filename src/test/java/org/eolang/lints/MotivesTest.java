/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.github.lombrozo.xnav.Xnav;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.cactoos.io.InputOf;
import org.eolang.parser.EoSyntax;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link Motives}.
 *
 * <p>Checks, for every lint, that its motive's "Incorrect:"/"Correct:"
 * examples demonstrate exactly what the motive claims: an "Incorrect:"
 * example must parse without errors and trigger the lint, a "Correct:" one
 * must parse without errors and trigger no defect of it. The one exception
 * is {@link #immuneToParseErrors(String)}: a lint whose only "Incorrect:"
 * shape is itself invalid EO under the current grammar (e.g. a test
 * attribute can't legally be the sole top-level object), the same way its
 * YAML pack tests it with {@code skip-errors: true}.</p>
 *
 * @since 0.0.1
 */
final class MotivesTest {

    @Test
    void motivesDemonstrateWhatTheyClaim() throws IOException {
        // @checkstyle ConditionalRegexpMultilineCheck (1 line)
        final List<String> failures = new ArrayList<>();
        for (final Lint lint : this.lints().values()) {
            for (final Example example : new Motives(lint.motive())) {
                this.check(lint, example, failures);
            }
        }
        MatcherAssert.assertThat(
            String.format(
                "Some motive examples don't demonstrate what they claim:%n%s",
                String.join(System.lineSeparator(), failures)
            ),
            failures,
            Matchers.empty()
        );
    }

    private Map<String, Lint> lints() {
        final Map<String, Lint> map = new HashMap<>();
        for (final Lint lint : new MonoLints()) {
            map.put(lint.name(), lint);
        }
        final LtUnlintNonExistingDefect extra = new LtUnlintNonExistingDefect(new MonoLints());
        map.put(extra.name(), extra);
        map.put("reserved-name", new LtReservedName(Map.of("true", "true.eo")));
        return map;
    }

    private void check(final Lint lint, final Example example, final List<String> failures) {
        try {
            final XML xmir;
            if ("eo".equals(example.lang())) {
                xmir = new EoSyntax(new InputOf(example.body())).parsed();
                final List<String> errors = new Xnav(xmir.inner())
                    .path("/object/errors/error")
                    .map(err -> err.text().orElse(""))
                    .collect(Collectors.toList());
                if (!errors.isEmpty() && !MotivesTest.immuneToParseErrors(lint.name())) {
                    failures.add(
                        String.format(
                            "%s: the %s example doesn't parse: %s",
                            lint.name(), example.label(), errors
                        )
                    );
                    return;
                }
            } else {
                xmir = new XMLDocument(example.body());
            }
            final int found = lint.defects(xmir).size();
            if (example.correct() && found > 0) {
                failures.add(
                    String.format(
                        "%s: the Correct example gets %d defect(s), expected none",
                        lint.name(), found
                    )
                );
            } else if (!example.correct() && found == 0) {
                failures.add(
                    String.format(
                        "%s: the Incorrect example gets no defect, expected at least one",
                        lint.name()
                    )
                );
            }
        } catch (final IOException ex) {
            failures.add(
                String.format(
                    "%s: the %s example failed: %s",
                    lint.name(), example.label(), ex.getMessage()
                )
            );
        }
    }

    private static boolean immuneToParseErrors(final String lint) {
        return Set.of("test-with-comment").contains(lint);
    }
}
