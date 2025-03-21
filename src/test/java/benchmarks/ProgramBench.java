/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package benchmarks;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import fixtures.LargeXmir;
import java.util.concurrent.TimeUnit;
import org.cactoos.scalar.Unchecked;
import org.eolang.lints.Project;
import org.eolang.lints.ProjectOf;
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
 * Benchmark for {@link Project}::singleDefects.
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
     * Large XMIR document.
     */
    private static final XML LARGE = new Unchecked<>(new LargeXmir()).value();

    /**
     * Small XMIR document.
     */
    private static final XML SMALL = new XMLDocument("<program name='foo'/>");

    @Benchmark
    public final void scansLargeXmir() {
        new ProjectOf(ProgramBench.LARGE).singleDefects();
    }

    @Benchmark
    public final void scansSmallXmir() {
        new ProjectOf(ProgramBench.SMALL).singleDefects();
    }
}
