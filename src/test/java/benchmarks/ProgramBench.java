/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package benchmarks;

import com.jcabi.xml.XML;
import fixtures.JavaToXmir;
import fixtures.ProgramBenches;
import java.util.concurrent.TimeUnit;
import org.cactoos.scalar.Unchecked;
import org.eolang.lints.Program;
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
 * Benchmark for {@link Program}.
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
public class ProgramBench {

    /**
     * Small XMIR document.
     */
    private static final XML SMALL = new Unchecked<>(
        new JavaToXmir(new ProgramBenches().value().get(ProgramBenches.ProgramSize.S))
    ).value();

    /**
     * Medium XMIR document.
     */
    private static final XML MEDIUM = new Unchecked<>(
        new JavaToXmir(new ProgramBenches().value().get(ProgramBenches.ProgramSize.M))
    ).value();

    /**
     * Large XMIR document.
     */
    private static final XML LARGE = new Unchecked<>(
        new JavaToXmir(new ProgramBenches().value().get(ProgramBenches.ProgramSize.L))
    ).value();

    /**
     * X-Large XMIR document.
     */
    private static final XML X_LARGE = new Unchecked<>(
        new JavaToXmir(new ProgramBenches().value().get(ProgramBenches.ProgramSize.XL))
    ).value();

    /**
     * XXL XMIR document.
     */
    private static final XML XXL = new Unchecked<>(
        new JavaToXmir(new ProgramBenches().value().get(ProgramBenches.ProgramSize.XXL))
    ).value();

    @Benchmark
    public final void scansSmallXmir() {
        new Program(ProgramBench.SMALL).defects();
    }

    @Benchmark
    public final void scansMediumXmir() {
        new Program(ProgramBench.MEDIUM).defects();
    }

    @Benchmark
    public final void scansLargeXmir() {
        new Program(ProgramBench.LARGE).defects();
    }

    @Benchmark
    public final void scansXlargeXmir() {
        new Program(ProgramBench.X_LARGE).defects();
    }

    @Benchmark
    public final void scansXxlXmir() {
        new Program(ProgramBench.XXL).defects();
    }
}
