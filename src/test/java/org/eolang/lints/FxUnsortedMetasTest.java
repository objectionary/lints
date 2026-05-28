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
        final Object input = pack.get("input");
        final XML fixed = new FxUnsortedMetas().apply(
            new EoProgram(String.valueOf(input.hashCode()), new InputOf(input.toString())).parse()
        );
        final Object output = pack.get("output");
        final XML expected = new EoProgram(
            String.valueOf(output.hashCode()),
            new InputOf(output.toString())
        ).parse();
        MatcherAssert.assertThat(
            "Meta heads after fix must match the expected order",
            fixed.xpath("/object/metas/meta/head/text()"),
            Matchers.equalTo(expected.xpath("/object/metas/meta/head/text()"))
        );
        MatcherAssert.assertThat(
            "Meta tails after fix must match the expected order",
            fixed.xpath("/object/metas/meta/tail/text()"),
            Matchers.equalTo(expected.xpath("/object/metas/meta/tail/text()"))
        );
    }
}
