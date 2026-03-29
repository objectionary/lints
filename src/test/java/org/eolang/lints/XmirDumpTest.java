/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import org.eolang.parser.EoSyntax;
import org.junit.jupiter.api.Test;

final class XmirDumpTest {
    @Test
    void dumpsXmir() throws Exception {
        final XML xmir = new EoSyntax(
            String.join(
                "\n",
                "+home https://github.com/objectionary/eo",
                "+package f",
                "+version 0.0.0",
                "",
                "# No comments.",
                "[] > main",
                "  QQ.io.stdout > @",
                "    \"Hello world\""
            )
        ).parsed();
        System.out.println("=== XMIR START ===");
        System.out.println(xmir.toString());
        System.out.println("=== XMIR END ===");
    }
}
