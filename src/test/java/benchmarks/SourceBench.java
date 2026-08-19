/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package benchmarks;

import fixtures.EoProgram;
import java.util.concurrent.TimeUnit;
import org.eolang.lints.Source;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

/**
 * Benchmark for {@link Source}.
 * @since 0.0.34
 * @checkstyle NonStaticMethodCheck (100 lines)
 */
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 1)
@Measurement(iterations = 3, time = 1, timeUnit = TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class SourceBench {

    /**
     * Ctor.
     */
    public SourceBench() {
        // nothing to do
    }

    /**
     * Benchmark for XMIR scanning.
     * Scans XMIR.
     * @param state State
     */
    @Benchmark
    public final void scansXmir(final BenchmarkState state) {
        new Source(state.xmir()).defects();
    }

    /**
     * Benchmark for XMIR scanning with {@code +unlint} metas present.
     * Scans XMIR.
     */
    @Benchmark
    public final void scansXmirWithUnlints() {
        new Source(
            new EoProgram("org/eolang/lints/unlint-ascii-only.eo").parse()
        ).defects();
    }
}
