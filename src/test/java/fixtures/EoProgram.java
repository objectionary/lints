/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package fixtures;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.jcabi.log.Logger;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.cactoos.io.ResourceOf;
import org.eolang.parser.EoSyntax;

/**
 * Parsed EO program from a classpath resource, with bounded in-memory caching.
 * @since 0.2.0
 */
public final class EoProgram {

    /**
     * Maximum number of cached entries.
     */
    private static final int MAX = 1000;

    /**
     * Cache of parsed XMIR documents, keyed by resource path.
     */
    private static final Cache<String, XML> CACHE = CacheBuilder.newBuilder()
        .maximumSize(EoProgram.MAX)
        .build();

    /**
     * Resource path.
     */
    private final String resource;

    /**
     * Constructor.
     * @param res Classpath resource path to the EO source file
     */
    public EoProgram(final String res) {
        this.resource = res;
    }

    /**
     * Parse the EO resource into XMIR, using the cache when available.
     * @return Parsed XMIR document
     */
    public XML parse() {
        try {
            return new XMLDocument(
                EoProgram.CACHE.get(
                    this.resource, () -> EoProgram.doParse(this.resource)
                ).deepCopy()
            );
        } catch (final ExecutionException ex) {
            throw new IllegalStateException(
                String.format("Failed to parse EO resource '%s'", this.resource),
                ex
            );
        }
    }

    /**
     * Perform the actual parse and log timing.
     * @param res Resource path to parse
     * @return Parsed XMIR document
     * @throws IOException If parsing fails
     */
    @SuppressWarnings("PMD.UnnecessaryLocalRule")
    private static XML doParse(final String res) throws IOException {
        final long start = System.currentTimeMillis();
        final XML xmir = new EoSyntax(new ResourceOf(res)).parsed();
        Logger.info(
            EoProgram.class,
            "Parsed '%s': %dms",
            res,
            System.currentTimeMillis() - start
        );
        return xmir;
    }
}
