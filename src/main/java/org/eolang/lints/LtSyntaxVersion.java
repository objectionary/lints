/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.github.lombrozo.xnav.Xnav;
import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.IoCheckedText;
import org.cactoos.text.TextOf;
import org.eolang.parser.OnDefault;

/**
 * Checks that the version specified in the +syntax meta is not
 * newer than the current parser version.
 *
 * <p>If the +syntax meta specifies a version higher than the parser's
 * version, it means the code requires a newer parser, and this 
 * lint reports an error.</p>
 *
 * @since 0.1.0
 */
final class LtSyntaxVersion implements Lint<XML> {

    /**
     * The parser version.
     */
    private final String parserVersion;

    /**
     * Default ctor.
     */
    LtSyntaxVersion() {
        this.parserVersion = "0.0.0";
    }

    /**
     * Ctor.
     * @param parserVersion The parser version.
     */
    LtSyntaxVersion(final String parserVersion) {
        this.parserVersion = parserVersion;
    }

    @Override
    public Collection<Defect> defects(final XML xmir) throws IOException {
        final Collection<Defect> defects = new ArrayList<>(0);
        final Xnav xml = new Xnav(xmir.inner());
        final List<Xnav> metas = xml.path("/object/metas/meta[head='syntax']")
            .collect(Collectors.toList());
        for (final Xnav meta : metas) {
            final String tail = meta.element("tail").text().orElse("");
            final String line = meta.attribute("line").text().orElse("0");
            if (!LtSyntaxVersion.valid(tail)) {
                defects.add(
                    new Defect.Default(
                        this.name(),
                        Severity.ERROR,
                        new OnDefault(xmir).get(),
                        Integer.parseInt(line),
                        String.format(
                            "The format of the +syntax meta is wrong: %s (SemVer expected instead)",
                            tail
                        )
                    )
                );
                continue;
            }
            if (this.compareVersions(tail) < 0) {
                defects.add(
                    new Defect.Default(
                        this.name(),
                        Severity.ERROR,
                        new OnDefault(xmir).get(),
                        Integer.parseInt(line),
                        String.format(
                            "The +syntax meta requires version %s, but the current parser version is %s (older)",
                            tail,
                            this.parserVersion
                        )
                    )
                );
            }
        }
        return defects;
    }

    @Override
    public String name() {
        return "syntax-version-mismatch";
    }

    @Override
    public String motive() throws IOException {
        return new IoCheckedText(
            new TextOf(
                new ResourceOf(
                    "org/eolang/motives/metas/syntax-version-mismatch.md"
                )
            )
        ).asString();
    }

    /**
     * Check if the version string is a valid SemVer.
     * @param version The version to validate.
     * @return True if valid.
     */
    private static boolean valid(final String version) {
        return version.matches("^\\d+\\.\\d+\\.\\d+(-[a-zA-Z0-9-]+)?$");
    }

    /**
     * Compare parser version to the given syntax version.
     * @param syntaxVersion The version from +syntax meta.
     * @return -1 if parser is older, 0 if equal, 1 if parser is newer.
     */
    private int compareVersions(final String syntaxVersion) {
        final int[] syntaxVer = LtSyntaxVersion.parts(syntaxVersion);
        final int[] parserVer = LtSyntaxVersion.parts(this.parserVersion);
        for (int i = 0; i < syntaxVer.length; i++) {
            if (parserVer[i] < syntaxVer[i]) {
                return -1;
            }
            if (parserVer[i] > syntaxVer[i]) {
                return 1;
            }
        }
        return 0;
    }

    /**
     * Parse the numeric parts of a SemVer string.
     * @param version The version string.
     * @return Array of [major, minor, patch].
     */
    private static int[] parts(final String version) {
        final String[] segments = version.split("-", 2)[0].split("\\.");
        return new int[] {
            segments.length > 0 ? Integer.parseInt(segments[0]) : 0,
            segments.length > 1 ? Integer.parseInt(segments[1]) : 0,
            segments.length > 2 ? Integer.parseInt(segments[2]) : 0
        };
    }
}
