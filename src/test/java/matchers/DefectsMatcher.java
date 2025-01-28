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

import com.jcabi.xml.XML;
import java.util.Collection;
import java.util.LinkedList;
import org.eolang.lints.Defect;
import org.eolang.lints.Severity;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

/**
 * Hamcrest matcher for defects in XML.
 *
 * @since 0.0.34
 */
public final class DefectsMatcher extends BaseMatcher<XML> {

    /**
     * Synthetic matcher that is built when input arrives.
     */
    private Matcher<Iterable<? extends Defect>> matcher;

    @Override
    public boolean matches(final Object xml) {
        final Collection<Defect> defects = new LinkedList<>();
        for (final XML defect : ((XML) xml).nodes("/defects/defect")) {
            defects.add(
                new Defect.Default(
                    "unknown",
                    Severity.parsed(defect.xpath("@severity").get(0)),
                    "unknown",
                    Integer.parseInt(defect.xpath("@line").get(0)),
                    defect.xpath("text()").get(0)
                )
            );
        }
        this.matcher = Matchers.everyItem(new DefectMatcher());
        return this.matcher.matches(defects);
    }

    @Override
    public void describeTo(final Description desc) {
        this.matcher.describeTo(desc);
    }
}
