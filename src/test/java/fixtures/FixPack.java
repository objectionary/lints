/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package fixtures;

import com.jcabi.xml.XML;
import java.util.Map;
import org.cactoos.io.InputOf;
import org.eolang.lints.Fix;
import org.yaml.snakeyaml.Yaml;

/**
 * A YAML test pack for a {@link Fix}, exposing the normalized fixed and
 * expected XMIR for comparison in tests.
 * @since 0.2.1
 */
public final class FixPack {

    /**
     * Parsed YAML pack with 'input' and 'output' EO programs.
     */
    private final Map<String, Object> pack;

    /**
     * The fix to apply to the input.
     */
    private final Fix fix;

    /**
     * Constructor.
     * @param yaml Raw YAML string containing 'input' and 'output' fields
     * @param fix The fix to apply
     */
    public FixPack(final String yaml, final Fix fix) {
        this((Map<String, Object>) new Yaml().load(yaml), fix);
    }

    /**
     * Constructor.
     * @param pack Parsed YAML map containing 'input' and 'output' fields
     * @param fix The fix to apply
     */
    private FixPack(final Map<String, Object> pack, final Fix fix) {
        this.pack = pack;
        this.fix = fix;
    }

    /**
     * Returns normalized XMIR after applying the fix to the input program.
     * @return Normalized XMIR string
     * @throws Exception If parsing or fix application fails
     */
    public String fixed() throws Exception {
        return FixPack.normalize(
            this.fix.apply(
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

    private static String normalize(final XML xmir) {
        return xmir.toString()
            .replaceAll("(?s)<!--.*?-->", "")
            .replaceAll("(?s)<listing>.*?</listing>", "<listing/>")
            .replaceAll(" time=\"[^\"]*\"", "")
            .replaceAll(" ms=\"[^\"]*\"", "")
            .replaceAll(" line=\"\\d+\"", "");
    }
}
