/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package matchers;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.languagetool.JLanguageTool;
import org.languagetool.Languages;
import org.languagetool.markup.AnnotatedText;
import org.languagetool.markup.AnnotatedTextBuilder;
import org.languagetool.rules.Rule;
import org.languagetool.rules.RuleMatch;
import org.languagetool.rules.spelling.SpellingCheckRule;

/**
 * Hamcrest matcher for a single piece of text, to make sure it's
 * grammatically valid.
 *
 * @since 0.0.34
 */
public final class GrammarMatcher extends BaseMatcher<String> {

    /**
     * The item to be matched.
     */
    private String input;

    /**
     * Errors.
     */
    private List<RuleMatch> errors;

    @Override
    public boolean matches(final Object obj) {
        final JLanguageTool tool = new JLanguageTool(
            Languages.getLanguageForShortCode("en-GB")
        );
        for (final Rule rule : tool.getAllActiveRules()) {
            if (rule instanceof SpellingCheckRule) {
                ((SpellingCheckRule) rule).addIgnoreTokens(
                    Arrays.asList("decoratee", "eolang", "spdx", "SPDX-compliant")
                );
            }
        }
        this.input = obj.toString();
        try {
            this.errors = tool.check(GrammarMatcher.annotated(this.input));
        } catch (final IOException ex) {
            throw new IllegalArgumentException(ex);
        }
        return this.errors.isEmpty();
    }

    @Override
    public void describeTo(final Description desc) {
        for (int idx = 0; idx < this.errors.size(); ++idx) {
            if (idx > 0) {
                desc.appendText(" and ");
            }
            final RuleMatch match = this.errors.get(idx);
            desc.appendText("\n")
                .appendValue(match.getRule().getId())
                .appendText(" (")
                .appendText(match.getMessage())
                .appendText(") at ")
                .appendText(Integer.toString(match.getFromPos()))
                .appendText("-")
                .appendText(Integer.toString(match.getToPos()))
                .appendText(" in:\n  ")
                .appendText(this.input)
                .appendText("\n  annotated input:\n  ")
                .appendText(GrammarMatcher.annotated(this.input).toString())
                .appendText("\n  suggested correction(s):\n  ")
                .appendValue(match.getSuggestedReplacements());
        }
    }

    private static AnnotatedText annotated(final String text) {
        final AnnotatedTextBuilder builder = new AnnotatedTextBuilder();
        final Pattern pattern = Pattern.compile("\"([^\"]+)\"");
        final Matcher matcher = pattern.matcher(text);
        int last = 0;
        while (matcher.find()) {
            final int start = matcher.start();
            if (start > last) {
                builder.addText(text.substring(last, start));
            }
            builder.addMarkup("\"").addText("text skipped").addMarkup("\"");
            last = matcher.end();
        }
        if (last < text.length()) {
            builder.addText(text.substring(last));
        }
        return builder.build();
    }
}
