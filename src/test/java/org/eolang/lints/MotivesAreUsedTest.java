/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.log.Logger;
import io.github.secretx33.resourceresolver.PathMatchingResourcePatternResolver;
import io.github.secretx33.resourceresolver.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test that ensures every motive markdown under
 * {@code org/eolang/motives/} is referenced by a registered lint.
 *
 * <p>Each lint exposes a motive document via its {@link Lint#motive()} method,
 * loaded from {@code src/main/resources/org/eolang/motives/{dimension}/{name}.md}.
 * If a markdown file lives under that tree but no lint claims it, the file is
 * orphaned: it ships in the JAR, contributes to the build, and confuses
 * future contributors who assume the lint exists. This test fails when such
 * orphans appear.</p>
 *
 * @since 0.1.0
 */
final class MotivesAreUsedTest {

    @Test
    void everyMotiveFileIsLinkedToALint() throws Exception {
        final Set<String> names = StreamSupport.stream(
            new PkMono().spliterator(), false
        ).map(Lint::name).collect(Collectors.toSet());
        final List<String> orphans = Arrays.stream(
            new PathMatchingResourcePatternResolver().getResources(
                "classpath*:org/eolang/motives/**/*.md"
            )
        ).map(MotivesAreUsedTest::shortName)
            .filter(name -> !names.contains(name))
            .distinct()
            .sorted()
            .collect(Collectors.toList());
        MatcherAssert.assertThat(
            Logger.format(
                "Motive files without a corresponding lint: %[list]s. Each .md under src/main/resources/org/eolang/motives/ must match the name() of a lint produced by PkMono.",
                orphans
            ),
            orphans,
            Matchers.empty()
        );
    }

    /**
     * Pull the lint name out of a motive resource filename.
     * @param res Classpath resource for a motive markdown file
     * @return Lint name (filename without {@code .md})
     */
    private static String shortName(final Resource res) {
        final String filename = res.getFilename();
        if (filename == null) {
            throw new IllegalStateException(
                String.format("Resource %s has no filename", res)
            );
        }
        return filename.replaceAll("\\.md$", "");
    }
}
