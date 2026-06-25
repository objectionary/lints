/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Throttled lint decorator.
 * <p>
 * Wraps a lint and limits its execution time to 10 seconds.
 * If the lint takes longer than the timeout, it is interrupted
 * and a single timeout defect is returned.
 * </p>
 * @since 0.0.47
 */
public final class ThrottledLint implements Lint {

    /**
     * Timeout in seconds.
     */
    private static final int TIMEOUT = 10;

    /**
     * The lint to throttle.
     */
    private final Lint decorated;

    /**
     * Ctor.
     * @param lnt The lint to decorate
     */
    public ThrottledLint(final Lint lnt) {
        this.decorated = lnt;
    }

    @Override
    public String name() {
        return this.decorated.name();
    }

    @Override
    public Collection<Defect> defects(final XML xmir) throws IOException {
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            final Future<Collection<Defect>> future = executor.submit(
                new Callable<Collection<Defect>>() {
                    @Override
                    public Collection<Defect> call() {
                        try {
                            return ThrottledLint.this.decorated.defects(xmir);
                        } catch (final IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
            );
            try {
                return future.get(ThrottledLint.TIMEOUT, TimeUnit.SECONDS);
            } catch (final TimeoutException ex) {
                future.cancel(true);
                return Collections.singletonList(
                    new Defect.Default(
                        this.name(),
                        Severity.WARNING,
                        0,
                        String.format(
                            "Lint '%s' exceeded %d second timeout",
                            this.name(),
                            ThrottledLint.TIMEOUT
                        )
                    )
                );
            } catch (final ExecutionException ex) {
                final Throwable cause = ex.getCause();
                if (cause instanceof IOException) {
                    throw (IOException) cause;
                }
                throw new IOException(
                    String.format(
                        "Lint '%s' failed with error: %s",
                        this.name(),
                        cause.getMessage()
                    ),
                    cause
                );
            } catch (final InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new IOException(
                    String.format(
                        "Lint '%s' was interrupted",
                        this.name()
                    ),
                    ex
                );
            }
        } finally {
            executor.shutdownNow();
        }
    }

    @Override
    public String motive() throws IOException {
        return this.decorated.motive();
    }

    @Override
    public Fix fix() {
        return this.decorated.fix();
    }
}
