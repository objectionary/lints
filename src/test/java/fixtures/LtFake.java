/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package fixtures;

import java.io.IOException;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;
import org.eolang.lints.Defect;
import org.eolang.lints.Lint;

/**
 * Fake class for Lint class.
 *
 * @param <T> The type of entity to analyze
 * @since 0.0.42
 */
@SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
public final class LtFake<T> implements Lint<T> {

    /**
     * Name supplier.
     */
    private final Supplier<String> name;

    /**
     * Defects supplier.
     */
    private final Function<T, Collection<Defect>> defects;

    /**
     * Motive supplier.
     */
    private final Supplier<String> motive;

    /**
     * Constructor.
     * @param name Lint name supplier.
     * @param defects Lint defects supplier.
     * @param motive Lint motive supplier.
     */
    public LtFake(
        final Supplier<String> name,
        final Function<T, Collection<Defect>> defects,
        final Supplier<String> motive
    ) {
        this.name = name;
        this.defects = defects;
        this.motive = motive;
    }

    /**
     * Constructor. Name and motive are provided by value.
     * @param name Lint name value.
     * @param defects Lint defects supplier.
     * @param motive Lint motive value.
     */
    public LtFake(
        final String name,
        final Function<T, Collection<Defect>> defects,
        final String motive
    ) {
        this(() -> name, defects, () -> motive);
    }

    /**
     * Constructor. Name and motive are hardcoded.
     * @param defects Lint defects supplier.
     */
    public LtFake(final Function<T, Collection<Defect>> defects) {
        this("Lint", defects, "Motive");
    }

    @Override
    public String name() {
        return this.name.get();
    }

    @Override
    public Collection<Defect> defects(final T entity) throws IOException {
        return this.defects.apply(entity);
    }

    @Override
    public String motive() throws IOException {
        return this.motive.get();
    }
}
