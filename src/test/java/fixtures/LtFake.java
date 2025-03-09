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
public final class LtFake<T> implements Lint<T> {

    /**
     * Name supplier.
     */
    private final Supplier<String> namesup;

    /**
     * Defects supplier.
     */
    private final Function<T, Collection<Defect>> defectssup;

    /**
     * Motive supplier.
     */
    private final Supplier<String> motivesup;

    /**
     * Constructor.
     * @param namesup Lint name supplier.
     * @param defectssup Lint defects supplier.
     * @param motivesup Lint motive supplier.
     */
    public LtFake(
        final Supplier<String> namesup,
        final Function<T, Collection<Defect>> defectssup,
        final Supplier<String> motivesup
    ) {
        this.namesup = namesup;
        this.defectssup = defectssup;
        this.motivesup = motivesup;
    }

    /**
     * Constructor. Name and motive are provided by value.
     * @param namesup Lint name value.
     * @param defectssup Lint defects supplier.
     * @param motivesup Lint motive value.
     */
    public LtFake(
        final String namesup,
        final Function<T, Collection<Defect>> defectssup,
        final String motivesup
    ) {
        this(() -> namesup, defectssup, () -> motivesup);
    }

    /**
     * Constructor. Name and motive are hardcoded.
     * @param defectssup Lint defects supplier.
     */
    public LtFake(final Function<T, Collection<Defect>> defectssup) {
        this("Lint", defectssup, "Motive");
    }

    @Override
    public String name() {
        return this.namesup.get();
    }

    @Override
    public Collection<Defect> defects(final T entity) throws IOException {
        return this.defectssup.apply(entity);
    }

    @Override
    public String motive() throws IOException {
        return this.motivesup.get();
    }
}
