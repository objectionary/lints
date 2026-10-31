/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package benchmarks;

import com.jcabi.xml.XML;
import fixtures.BytecodeClass;
import fixtures.SourceSize;
import org.cactoos.scalar.Unchecked;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

/**
 * Benchmark state.
 *
 * @since 0.0.45
 */
@State(Scope.Benchmark)
public class BenchmarkState {

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
     * Ctor.
     */
    public BenchmarkState() {
        // nothing to do
    }

    /**
     * Initialize the state.
     */
    @Setup(Level.Trial)
    public void init() {
        final SourceSize psize = SourceSize.valueOf(this.size);
        this.xmir = new Unchecked<>(
            new BytecodeClass(psize.name(), psize.java())
        ).value();
    }

    /**
     * The XMIR to scan.
     *
     * @return The XMIR
     */
    public XML xmir() {
        return this.xmir;
    }
}
