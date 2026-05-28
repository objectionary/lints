/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import fixtures.EoProgram;
import java.util.Map;
import org.cactoos.io.InputOf;
import org.eolang.jucs.ClasspathSource;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.yaml.snakeyaml.Yaml;

/**
 * Tests for {@link FxUnsortedMetas}.
 * @since 0.2.1
 */
final class FxUnsortedMetasTest {

    @ParameterizedTest
    @ClasspathSource(value = "org/eolang/lints/fixes/unsorted-metas/", glob = "**.yaml")
    void fixesUnsortedMetas(final String yaml) throws Exception {
        final Map<String, Object> pack = new Yaml().load(yaml);
        final XML fixed = new FxUnsortedMetas().apply(
            new EoProgram(
                String.valueOf(pack.get("input").hashCode()),
                new InputOf(pack.get("input").toString())
            ).parse()
        );
        final XML expected = new EoProgram(
            String.valueOf(pack.get("output").hashCode()),
            new InputOf(pack.get("output").toString())
        ).parse();
        MatcherAssert.assertThat(
            "Full XMIR after fix must exactly match the expected XMIR",
            FxUnsortedMetasTest.normalize(fixed),
            Matchers.equalTo(FxUnsortedMetasTest.normalize(expected))
        );
    }

    private static String normalize(final XML xmir) {
        return xmir.toString()
            .replaceAll("(?s)<!--.*?-->", "")
            .replaceAll("(?s)<listing>.*?</listing>", "<listing/>")
            .replaceAll(" time=\"[^\"]*\"", "")
            .replaceAll(" ms=\"[^\"]*\"", "")
            .replaceAll(" line=\"\\d+\"", "");
    }
}
