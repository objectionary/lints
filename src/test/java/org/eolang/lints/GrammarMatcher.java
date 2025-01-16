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
package org.eolang.lints;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.languagetool.JLanguageTool;
import org.languagetool.Languages;
import org.languagetool.markup.AnnotatedText;
import org.languagetool.markup.AnnotatedTextBuilder;
import org.languagetool.rules.RuleMatch;

/**
 * Hamcrest matcher for a single piece of text, to make sure it's
 * grammatically valid.
 *
 * @since 0.0.34
 */
final class GrammarMatcher extends BaseMatcher<String> {

    /**
     * The item to be matched.
     */
    private String input;

    /**
     * Errors.
     */
    private List<RuleMatch> matches;

    @Override
    public boolean matches(final Object obj) {
        final JLanguageTool tool = new JLanguageTool(
            Languages.getLanguageForShortCode("en-GB")
        );
        this.input = obj.toString();
        try {
            this.matches = tool.check(GrammarMatcher.annonated(this.input));
        } catch (final IOException ex) {
            throw new IllegalArgumentException(ex);
        }
        return this.matches.isEmpty();
    }

    @Override
    public void describeTo(final Description desc) {
        for (int idx = 0; idx < this.matches.size(); ++idx) {
            if (idx > 0) {
                desc.appendText(" and ");
            }
            final RuleMatch match = this.matches.get(idx);
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
                .appendText(GrammarMatcher.annonated(this.input).toString())
                .appendText("\n  suggested correction(s):\n  ")
                .appendValue(match.getSuggestedReplacements());
        }
    }

    private static AnnotatedText annonated(final String text) {
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
