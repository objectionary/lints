/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.github.lombrozo.xnav.Xnav;
import com.jcabi.http.Request;
import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import com.jcabi.xml.XML;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;
import org.eolang.parser.EoSyntax;

/**
 * Lint for reserved names.
 *
 * @since 0.0.43
 */
final class LtReservedName implements Lint<XML> {

    /**
     * Objects URL.
     */
    private static final String OBJECTS_CATALOG =
        "https://api.github.com/repos/objectionary/home/contents/objects/org/eolang";

    /**
     * Reserved names.
     */
    private final Map<String, String> reserved;

    /**
     * Ctor.
     */
    LtReservedName() {
        this(LtReservedName.reservedInHome());
    }

    /**
     * Ctor.
     *
     * @param names Reserved names
     */
    LtReservedName(final Map<String, String> names) {
        this.reserved = names;
    }

    @Override
    public String name() {
        return "reserved-name";
    }

    @Override
    public Collection<Defect> defects(final XML xmir) throws IOException {
        final Collection<Defect> defects = new LinkedList<>();
        final Xnav program = new Xnav(xmir.inner());
        program.path("//o[@name]")
            .forEach(
                object -> {
                    final String oname = object.attribute("name").text().get();
                    if (this.reserved.keySet().contains(oname)) {
                        defects.add(
                            new Defect.Default(
                                this.name(),
                                Severity.WARNING,
                                program.element("program").attribute("name")
                                    .text().orElse("unknown"),
                                Integer.parseInt(object.attribute("line").text().orElse("0")),
                                String.format(
                                    "Object name \"%s\" is already reserved by object in the \"%s\"",
                                    oname, this.reserved.get(oname)
                                )
                            )
                        );
                    }
                }
            );
        return defects;
    }

    @Override
    public String motive() throws IOException {
        return new UncheckedText(
            new TextOf(
                new ResourceOf(
                    String.format(
                        "org/eolang/motives/names/%s.md", this.name()
                    )
                )
            )
        ).asString();
    }

    private static Map<String, String> reservedInHome() {
        final Map<String, String> sources = new HashMap<>(64);
        final Map<String, String> parse = new HashMap<>(64);
        final Queue<String> directories = new LinkedList<>();
        directories.add(LtReservedName.OBJECTS_CATALOG);
        while (!directories.isEmpty()) {
            LtReservedName.unpack(directories.poll(), parse, directories);
        }
        parse.forEach(
            (source, path) -> sources.put(
                LtReservedName.source(source),
                String.format(
                    "%s%s",
                    "org.eolang",
                    path.replace(LtReservedName.OBJECTS_CATALOG, "")
                        .replace("?ref=master", "").replace("/", ".")
                )
            )
        );
        final Map<String, String> names = new HashMap<>(64);
        sources.forEach(
            (src, path) -> {
                final XML parsed;
                try {
                    parsed = new EoSyntax("reserved", src).parsed();
                    new Xnav(parsed.inner()).path("/program/objects/o/@name")
                        .map(oname -> oname.text().get())
                        .forEach(oname -> names.put(oname, path));
                } catch (final IOException exception) {
                    throw new IllegalStateException("Failed to parse EO sources", exception);
                }
            }
        );
        return names;
    }

    private static void unpack(
        final String dir, final Map<String, String> parse, final Queue<String> dirs
    ) {
        try {
            final JsonArray refs = Json.createReader(
                new StringReader(
                    new JdkRequest(dir)
                        .header("Accept", "application/vnd.github.v3+json")
                        .method(Request.GET)
                        .fetch()
                        .as(RestResponse.class)
                        .body()
                )
            ).readArray();
            for (int pos = 0; pos < refs.size(); pos += 1) {
                final JsonObject ref = refs.getJsonObject(pos);
                final String name = ref.getString("name");
                final String type = ref.getString("type");
                if ("file".equals(type) && name.endsWith(".eo")) {
                    parse.put(ref.getString("path"), ref.getString("url"));
                } else if ("dir".equals(type)) {
                    dirs.add(ref.getString("url"));
                }
            }
        } catch (final IOException exception) {
            throw new IllegalStateException(
                "Failed to fetch EO source from home", exception
            );
        }
    }

    private static String source(final String path) {
        try {
            return new JdkRequest(
                String.format(
                    "https://raw.githubusercontent.com/objectionary/home/refs/heads/master/%s", path
                )
            ).method(Request.GET).fetch().as(RestResponse.class).body();
        } catch (final IOException exception) {
            throw new IllegalStateException(
                String.format("Failed to find source EO file from %s", path),
                exception
            );
        }
    }
}
