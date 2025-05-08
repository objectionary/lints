/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package benchmarks;

import com.jcabi.xml.XML;
import fixtures.BytecodeClass;
import fixtures.SourceSize;
import java.util.concurrent.TimeUnit;
import org.cactoos.scalar.Unchecked;
import org.eolang.lints.Source;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

/**
 * Benchmark for {@link Source}.
 *
 * @since 0.0.34
 * @checkstyle DesignForExtensionCheck (10 lines)
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
     * Scans XMIR.
     * @param state State
     * @todo #555:35min Enable `duplicate-names-in-diff-context` benchmark.
     *  Currently, its slow, especially for `L` and `XL`-sized sources. Let's optimize it,
     *  in order to enable this benchmark.
     */
    @Benchmark
    public final void scansXmir(final BenchmarkState state) {
        new Source(state.xmir).without("duplicate-names-in-diff-context").defects();
    }

    /**
     * Benchmark state.
     * @since 0.0.45
     */
    @State(Scope.Benchmark)
    public static class BenchmarkState {

        /**
         * Source size.
         */
        @Param({"S", "M", "L", "XL", "XXL"})
        private String size;

        /**
         * XMIR.
         */
        private XML xmir;

        /**
         * Initialize the state.
         */
        @Setup(Level.Trial)
        public void init() {
            this.xmir = new Unchecked<>(new BytecodeClass(SourceSize.valueOf(this.size))).value();
        }
    }
}
