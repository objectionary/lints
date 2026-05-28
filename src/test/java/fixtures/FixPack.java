/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package fixtures;

import com.jcabi.xml.XML;
import java.util.List;
import java.util.Map;
import org.cactoos.io.InputOf;
import org.eolang.lints.Fix;
import org.eolang.lints.FxByXsl;
import org.yaml.snakeyaml.Yaml;

/**
 * A YAML test pack for a fix, exposing the normalized fixed and
 * expected XMIR for comparison in tests.
 * @since 0.2.1
 */
public final class FixPack {

    /**
     * Parsed YAML pack with 'sheets', 'input' and 'output' fields.
     */
    private final Map<String, Object> pack;

    /**
     * Constructor.
     * @param yaml Raw YAML string containing 'sheets', 'input' and 'output' fields
     * @checkstyle ConstructorsCodeFreeCheck (5 lines)
     */
    @SuppressWarnings("unchecked")
    public FixPack(final String yaml) {
        this((Map<String, Object>) new Yaml().load(yaml));
    }

    /**
     * Constructor.
     * @param pack Parsed YAML map
     */
    private FixPack(final Map<String, Object> pack) {
        this.pack = pack;
    }

    /**
     * Returns normalized XMIR after applying the fix to the input program.
     * @return Normalized XMIR string
     * @throws Exception If parsing or fix application fails
     */
    public String fixed() throws Exception {
        return FixPack.normalize(
            this.fix().apply(
                new EoProgram(
                    String.valueOf(this.pack.get("input").hashCode()),
                    new InputOf(this.pack.get("input").toString())
                ).parse()
            )
        );
    }

    /**
     * Returns normalized XMIR of the expected output program.
     * @return Normalized XMIR string
     */
    public String expected() {
        return FixPack.normalize(
            new EoProgram(
                String.valueOf(this.pack.get("output").hashCode()),
                new InputOf(this.pack.get("output").toString())
            ).parse()
        );
    }

    private Fix fix() {
        return new FxByXsl((List<String>) this.pack.get("sheets"));
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
