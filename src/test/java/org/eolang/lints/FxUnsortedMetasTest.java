/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import fixtures.EoProgram;
import java.util.Map;
import java.util.stream.Collectors;
import org.cactoos.io.InputOf;
import org.eolang.jucs.ClasspathSource;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.yaml.snakeyaml.Yaml;

/**
 * Tests for {@link FxUnsortedMetas}.
 * @since 0.2.1
 * @todo #896:60min Replace partial meta comparison with exact XMIR match.
 *  Currently the test only compares the ordered sequence of "head tail"
 *  strings extracted from the metas section. Since we have a clear 'input'
 *  and 'output' EO program in each YAML pack, we should compare the full
 *  XMIR structure of the fixed result against the expected XMIR, ignoring
 *  only volatile attributes such as @line numbers.
 */
final class FxUnsortedMetasTest {

    @ParameterizedTest
    @ClasspathSource(value = "org/eolang/lints/fixes/unsorted-metas/", glob = "**.yaml")
    void fixesUnsortedMetas(final String yaml) throws Exception {
        final Map<String, Object> pack = new Yaml().load(yaml);
        final Object input = pack.get("input");
        final Object output = pack.get("output");
        MatcherAssert.assertThat(
            "Metas after fix must match the expected order",
            new FxUnsortedMetas().apply(
                new EoProgram(
                    String.valueOf(input.hashCode()),
                    new InputOf(input.toString())
                ).parse()
            ).nodes("/object/metas/meta").stream().map(
                m -> String.format(
                    "%s %s",
                    m.xpath("head/text()").get(0),
                    m.xpath("tail/text()").get(0)
                )
            ).collect(Collectors.toList()),
            Matchers.equalTo(
                new EoProgram(
                    String.valueOf(output.hashCode()),
                    new InputOf(output.toString())
                ).parse().nodes("/object/metas/meta").stream().map(
                    m -> String.format(
                        "%s %s",
                        m.xpath("head/text()").get(0),
                        m.xpath("tail/text()").get(0)
                    )
                ).collect(Collectors.toList())
            )
        );
    }
}
