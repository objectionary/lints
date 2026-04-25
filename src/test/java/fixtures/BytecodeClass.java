/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package fixtures;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import com.yegor256.farea.Farea;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicReference;
import org.cactoos.Scalar;
import org.cactoos.bytes.BytesOf;
import org.cactoos.bytes.UncheckedBytes;
import org.cactoos.io.ResourceOf;
import org.xembly.Directives;
import org.xembly.Xembler;

/**
 * XMIR document from Java bytecode.
 * @since 0.0.31
 */
public final class BytecodeClass implements Scalar<XML> {

    /**
     * Name of the program.
     */
    private final String name;

    /**
     * Java class to transform.
     */
    private final String java;

    /**
     * Constructor.
     * @param jclass Java class to transform
     */
    public BytecodeClass(final String jclass) {
        this("unknown", jclass);
    }

    /**
     * Constructor.
     * @param nme Program name
     * @param jclass Java class to transform
     */
    public BytecodeClass(final String nme, final String jclass) {
        this.name = nme;
        this.java = jclass;
    }

    @Override
    public XML value() throws Exception {
        final AtomicReference<XML> ref = new AtomicReference<>();
        new Farea(Files.createTempDirectory("tmp")).together(f -> this.disassemble(f, ref));
        final XML xml = ref.get();
        new Xembler(
            new Directives().xpath("/program").attr("name", this.name)
        ).apply(xml.inner());
        return xml;
    }

    /**
     * Disassemble the bytecode into XMIR.
     * @param farea Farea instance
     * @param ref Reference to store the result
     * @throws IOException If fails
     */
    private void disassemble(final Farea farea, final AtomicReference<XML> ref) throws IOException {
        farea.clean();
        farea.files().file(String.format("target/classes/%s", this.java)).write(
            new UncheckedBytes(
                new BytesOf(
                    new ResourceOf(this.java)
                )
            ).asBytes()
        );
        farea.build()
            .plugins()
            .append("org.eolang", "jeo-maven-plugin", "0.10.0")
            .execution("default")
            .phase("process-classes")
            .goals("disassemble")
            .configuration().set("skipVerification", true);
        farea.exec("process-classes");
        ref.set(
            new XMLDocument(
                farea.files().file(
                    String.format(
                        "target/generated-sources/jeo-xmir/%s.xmir",
                        this.java.replace(".class", "")
                    )
                ).path()
            )
        );
    }
}
