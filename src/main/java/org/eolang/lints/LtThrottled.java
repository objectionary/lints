/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Lint decorator that gives up on the decoratee when it runs for too long.
 *
 * <p>When the decoratee does not finish in time, a single warning is reported
 * instead of its defects, under the name of the decoratee, thus it may be
 * suppressed with {@code +unlint}, as any other defect of that lint. The
 * decoratee is interrupted, but an XSL transformation does not react to
 * interruption, so it may keep running in a daemon thread until it is done,
 * while the build goes on.</p>
 *
 * <p>This class is thread-safe.</p>
 *
 * @since 0.6.1
 */
final class LtThrottled implements Lint {

    /**
     * How long a lint may run by default.
     */
    private static final Duration DEFAULT = Duration.ofSeconds(10L);

    /**
     * Threads the decoratees run in, which never keep the JVM alive.
     */
    private static final ExecutorService THREADS = Executors.newCachedThreadPool(
        task -> {
            final Thread thread = new Thread(task, "lt-throttled");
            thread.setDaemon(true);
            return thread;
        }
    );

    /**
     * Object wrapped by a decorator.
     */
    private final Lint origin;

    /**
     * How long the decoratee may run.
     */
    private final Duration timeout;

    /**
     * Ctor.
     *
     * @param origin Object wrapped by a decorator
     */
    LtThrottled(final Lint origin) {
        this(origin, LtThrottled.DEFAULT);
    }

    /**
     * Ctor.
     *
     * @param origin Object wrapped by a decorator
     * @param timeout How long the decoratee may run
     */
    LtThrottled(final Lint origin, final Duration timeout) {
        this.origin = origin;
        this.timeout = timeout;
    }

    @Override
    public String name() {
        return this.origin.name();
    }

    @Override
    public Collection<Defect> defects(final XML xmir) throws IOException {
        final Future<Collection<Defect>> future = LtThrottled.THREADS.submit(
            () -> this.origin.defects(xmir)
        );
        Collection<Defect> defects;
        try {
            defects = future.get(this.timeout.toMillis(), TimeUnit.MILLISECONDS);
        } catch (final TimeoutException ex) {
            future.cancel(true);
            defects = Collections.singletonList(
                new Defect.Default(
                    this.origin.name(),
                    Severity.WARNING,
                    0,
                    String.format(
                        "The lint did not finish in %d ms and was stopped, so its defects are unknown",
                        this.timeout.toMillis()
                    )
                )
            );
        } catch (final InterruptedException ex) {
            future.cancel(true);
            Thread.currentThread().interrupt();
            throw new IOException(
                String.format("Interrupted while running the '%s' lint", this.origin.name()),
                ex
            );
        } catch (final ExecutionException ex) {
            throw LtThrottled.propagate(ex);
        }
        return defects;
    }

    @Override
    public String motive() throws IOException {
        return this.origin.motive();
    }

    @Override
    public Fix fix() {
        return this.origin.fix();
    }

    private static IOException propagate(final ExecutionException exc) {
        final Throwable cause = exc.getCause();
        if (cause instanceof RuntimeException) {
            throw (RuntimeException) cause;
        }
        if (cause instanceof Error) {
            throw (Error) cause;
        }
        final IOException result;
        if (cause instanceof IOException) {
            result = (IOException) cause;
        } else {
            result = new IOException(cause);
        }
        return result;
    }
}
