/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package fixtures;

import com.jcabi.xml.XML;
import com.yegor256.xsline.Xsline;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.eolang.xax.Xtory;

/**
 * A story with a short defect-count assertion.
 *
 * @since 1.0
 */
public final class XtDefects implements Xtory {

    /**
     * Original story.
     */
    private final Xtory origin;

    /**
     * New story.
     *
     * @param story Original story
     */
    public XtDefects(final Xtory story) {
        this.origin = story;
    }

    @Override
    public Map<String, Object> map() {
        return this.origin.map();
    }

    @Override
    public XML before() {
        return this.origin.before();
    }

    @Override
    public XML after() {
        return this.origin.after();
    }

    @Override
    public Xsline xsline() {
        return this.origin.xsline();
    }

    @Override
    public Collection<String> asserts() {
        final Collection<String> assertions =
            new ArrayList<>(this.origin.asserts());
        final Object defects = this.map().get("defects");
        if (defects instanceof List) {
            final List<?> list = (List<?>) defects;
            assertions.add(
                String.format("/defects[count(defect)=%d]", list.size())
            );
            for (final Object entry : list) {
                assertions.add(XtDefects.defect((Map<?, ?>) entry));
            }
        } else if (defects != null) {
            assertions.add(
                String.format(
                    "/defects[count(defect)=%s]",
                    defects
                )
            );
        }
        return assertions;
    }

    private static String defect(final Map<?, ?> entry) {
        final Collection<String> predicates = new ArrayList<>(entry.size());
        final Object severity = entry.get("severity");
        if (severity != null) {
            predicates.add(String.format("@severity='%s'", severity));
        }
        final Object line = entry.get("line");
        if (line != null) {
            predicates.add(String.format("@line='%s'", line));
        }
        final Object rule = entry.get("rule");
        if (rule != null) {
            predicates.add(String.format("@rule='%s'", rule));
        }
        return String.format(
            "/defects/defect[%s]",
            String.join(" and ", predicates)
        );
    }
}
