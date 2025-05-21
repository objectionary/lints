/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package fixtures;

/**
 * Program size.
 * @since 0.0.45
 */
public enum SourceSize {

    /**
     * Standard source.
     */
    S("S", 5, 50, "com/sun/jna/PointerType.class"),

    /**
     * Medium-sized source.
     */
    M("M", 90, 200, "com/sun/jna/Memory.class"),

    /**
     * Large-size source.
     */
    L("L", 400, 700, "com/sun/jna/Pointer.class"),

    /**
     * Extra-large source.
     */
    XL("XL", 800, 1200, "com/sun/jna/Structure.class"),

    /**
     * XXL source.
     */
    XXL(
        "XXL",
        1500,
        Integer.MAX_VALUE,
        "org/apache/hadoop/hdfs/server/namenode/FSNamesystem.class"
    );

    /**
     * Source largeness.
     */
    private final String largeness;

    /**
     * Min size, in executable lines.
     */
    private final int min;

    /**
     * Max size, in executable lines.
     */
    private final int max;

    /**
     * Java '.class' path.
     */
    private final String jclass;

    /**
     * Ctor.
     * @param txt Txt largeness
     * @param start Minimum size in executable lines
     * @param end Maximum size in executable lines
     * @param java Java bytecode class path
     * @checkstyle ParameterNumberCheck (3 lines)
     */
    SourceSize(final String txt, final int start, final int end, final String java) {
        this.largeness = txt;
        this.min = start;
        this.max = end;
        this.jclass = java;
    }

    /**
     * Source largeness.
     * @return Program type as string
     */
    public String type() {
        return this.largeness;
    }

    /**
     * Min allowed count of executable lines.
     * @return Lines count
     */
    public int minAllowed() {
        return this.min;
    }

    /**
     * Max allowed count of executable lines.
     * @return Lines count
     */
    public int maxAllowed() {
        return this.max;
    }

    /**
     * Java path.
     * @return Path to Java bytecode class
     */
    public String java() {
        return this.jclass;
    }
}
