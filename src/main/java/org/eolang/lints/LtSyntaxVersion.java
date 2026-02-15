/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.github.lombrozo.xnav.Xnav;
import com.google.common.base.Splitter;
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
    private final String parser;

    /**
     * Default ctor.
     */
    LtSyntaxVersion() {
        this("0.0.0");
    }

    /**
     * Ctor.
     * @param ver The parser version (must be valid SemVer).
     */
    @SuppressWarnings("PMD.ConstructorOnlyInitializesOrCallOtherConstructors")
    LtSyntaxVersion(final String ver) {
        if (ver == null || !LtSyntaxVersion.valid(ver)) {
            throw new IllegalArgumentException(
                String.format(
                    "parser version must be valid SemVer (e.g. 1.2.3), got: %s",
                    LtSyntaxVersion.display(ver)
                )
            );
        }
        this.parser = ver;
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
            if (this.newer(tail)) {
                defects.add(
                    new Defect.Default(
                        this.name(),
                        Severity.ERROR,
                        new OnDefault(xmir).get(),
                        Integer.parseInt(line),
                        String.format(
                            "The +syntax meta requires version %s, but the current parser version is %s (older)",
                            tail,
                            this.parser
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
     * Display a version value for error messages.
     * @param ver The version value, possibly null.
     * @return Formatted display string.
     */
    private static String display(final String ver) {
        final String result;
        if (ver == null) {
            result = "null";
        } else {
            result = String.format("\"%s\"", ver);
        }
        return result;
    }

    /**
     * Check if the version string is a valid SemVer.
     * @param ver The version to validate.
     * @return True if valid.
     */
    private static boolean valid(final String ver) {
        return ver.matches("^\\d+\\.\\d+\\.\\d+(-[a-zA-Z0-9-]+)?$");
    }

    /**
     * Check if the syntax version is newer than the parser version.
     * @param syntax The version from +syntax meta.
     * @return True if parser version is older than syntax version.
     */
    private boolean newer(final String syntax) {
        final int[] left = LtSyntaxVersion.parts(this.parser);
        final int[] right = LtSyntaxVersion.parts(syntax);
        final int major = Integer.compare(left[0], right[0]);
        final int minor = Integer.compare(left[1], right[1]);
        final int patch = Integer.compare(left[2], right[2]);
        final boolean result;
        if (major == 0 && minor == 0) {
            result = patch < 0;
        } else if (major == 0) {
            result = minor < 0;
        } else {
            result = major < 0;
        }
        return result;
    }

    /**
     * Parse the numeric parts of a SemVer string.
     * @param ver The version string.
     * @return Array of major, minor, patch.
     */
    private static int[] parts(final String ver) {
        final List<String> segments = Splitter.on('.').splitToList(
            ver.split("-", 2)[0]
        );
        return new int[]{
            Integer.parseInt(segments.get(0)),
            Integer.parseInt(segments.get(1)),
            Integer.parseInt(segments.get(2)),
        };
    }
}
