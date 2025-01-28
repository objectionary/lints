/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2025 Objectionary.com
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
