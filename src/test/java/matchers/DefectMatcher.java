/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package matchers;

import java.util.ArrayList;
import java.util.List;
import org.eolang.lints.Defect;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

/**
 * Hamcrest matcher for a single defect.
 *
 * @since 0.0.34
 */
public final class DefectMatcher extends BaseMatcher<Defect> {

    /**
     * Synthetic matcher that is built when input arrives.
     */
    private final List<Matcher<?>> matchers = new ArrayList<>(0);

    @Override
    public boolean matches(final Object input) {
        final Defect defect = (Defect) input;
        return this.saved(Matchers.greaterThanOrEqualTo(0)).matches(defect.line())
            && this.saved(Matchers.not(Matchers.emptyString())).matches(defect.text())
            && this.saved(Matchers.not(Matchers.endsWith("."))).matches(defect.text())
            && this.saved(new GrammarMatcher()).matches(defect.text());
    }

    @Override
    public void describeTo(final Description desc) {
        for (int idx = 0; idx < this.matchers.size(); ++idx) {
            if (idx > 0) {
                desc.appendText(" and ");
            }
            final Matcher<?> matcher = this.matchers.get(idx);
            matcher.describeTo(desc);
        }
    }

    private Matcher<?> saved(final Matcher<?> matcher) {
        this.matchers.add(matcher);
        return matcher;
    }
}
