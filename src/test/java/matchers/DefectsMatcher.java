/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package matchers;

import com.jcabi.xml.XML;
import java.util.ArrayList;
import java.util.Collection;
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
        final Collection<Defect> defects = new ArrayList<>(0);
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
