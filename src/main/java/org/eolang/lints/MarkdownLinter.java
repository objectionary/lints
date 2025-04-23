/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import java.io.Closeable;
import java.util.stream.Stream;
import org.cactoos.io.ResourceOf;
import org.cactoos.io.UncheckedInput;
import org.cactoos.text.FormattedText;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

/**
 * Markdownlint interop. It can take a very long time to create.
 *
 * @since 0.0.47
 */
final class MarkdownLinter implements Closeable {
    /**
     * JavaScript code of markdownlint with function, that run markdownlint on single string.
     */
    private static final String JSCODE = String.format(
        "%s%s",
        "globalThis.URL=class{constructor(input){this.href=input;}};",
        new UncheckedText(
            new TextOf(
                new UncheckedInput(
                    new ResourceOf("markdownlint.js")
                )
            )
        ).asString()
    );

    /**
     * JavaScripts context with markdownlint.
     */
    private final Context context;

    /**
     * Function lint from JavaScript, that accept string and return rules violations.
     */
    private final Value mdlint;

    @SuppressWarnings("PMD.ConstructorOnlyInitializesOrCallOtherConstructors")
    MarkdownLinter() {
        this.context = Context.newBuilder("js")
            .option("engine.WarnInterpreterOnly", "false")
            .build();
        this.context.eval("js", MarkdownLinter.JSCODE);
        this.mdlint = this.context.getBindings("js").getMember("lint");
    }

    public Stream<Defect.Default> defects(final String text) {
        final Stream.Builder<Defect.Default> defects = Stream.builder();
        final Value errors = this.mdlint.execute(text);
        for (int idx = 0; idx < errors.getArraySize(); idx += 1) {
            final Value error = errors.getArrayElement(idx);
            final String rule = rule(error.getMember("ruleNames"));
            defects.add(
                new Defect.Default(
                    rule,
                    Severity.WARNING,
                    error.getMember("lineNumber").asInt(),
                    new UncheckedText(
                        new FormattedText(
                            "[%s] %s. See %s",
                            rule,
                            error.getMember("ruleDescription").asString(),
                            error.getMember("ruleInformation").asString()
                        )
                    ).asString()
                )
            );
        }
        return defects.build();
    }

    @Override
    public void close() {
        this.context.close();
    }

    /**
     * MD rule.
     *
     * @param names Javascript array of names.
     * @return The first name starting with MD.
     * @throws IllegalStateException If no name starting with MD is found.
     */
    private static String rule(final Value names) {
        for (int idx = 0; idx < names.getArraySize(); idx += 1) {
            final String name = names.getArrayElement(idx).asString();
            if (name.startsWith("MD")) {
                return name;
            }
        }
        throw new IllegalStateException("cannot find name of markdown lint starting with MD");
    }
}
