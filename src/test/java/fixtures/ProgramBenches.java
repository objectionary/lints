/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package fixtures;

import java.util.LinkedHashMap;
import java.util.Map;
import org.cactoos.Scalar;

/**
 * Program benches.
 * @since 0.0.45
 */
public final class ProgramBenches implements Scalar<Map<ProgramBenches.ProgramSize, String>> {

    @Override
    public Map<ProgramBenches.ProgramSize, String> value() {
        final Map<ProgramBenches.ProgramSize, String> programs = new LinkedHashMap<>(4);
        programs.put(ProgramBenches.ProgramSize.S, "com/sun/jna/PointerType.class");
        programs.put(ProgramBenches.ProgramSize.M, "com/sun/jna/Memory.class");
        programs.put(ProgramBenches.ProgramSize.L, "com/sun/jna/Pointer.class");
        programs.put(ProgramBenches.ProgramSize.XL, "com/sun/jna/Structure.class");
        programs.put(
            ProgramBenches.ProgramSize.XXL,
            "org/apache/hadoop/hdfs/server/namenode/FSNamesystem.class"
        );
        return programs;
    }

    /**
     * Program size.
     * @since 0.0.45
     */
    public enum ProgramSize {

        /**
         * Standard program.
         */
        S("S", 5, 50),
        /**
         * Medium-sized program.
         */
        M("M", 90, 200),
        /**
         * Large-size program.
         */
        L("L", 400, 700),

        /**
         * Extra-large program.
         */
        XL("XL", 800, 1200),

        /**
         * XXL program.
         */
        XXL("XXL", 1500, Integer.MAX_VALUE);

        /**
         * Size marker.
         */
        private final String marker;

        /**
         * Min size, in executable lines.
         */
        private final int min;

        /**
         * Max size, in executable lines.
         */
        private final int max;

        /**
         * Ctor.
         * @param txt Txt marker
         * @param start Minimum size in executable lines
         * @param end Maximum size in executable lines
         */
        ProgramSize(final String txt, final int start, final int end) {
            this.marker = txt;
            this.min = start;
            this.max = end;
        }

        /**
         * Marker size.
         * @return Marker
         */
        public String size() {
            return this.marker;
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
    }
}
