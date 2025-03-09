package org.eolang.lints;

import java.io.IOException;
import java.util.Collection;
import org.cactoos.func.StickyFunc;
import org.cactoos.func.UncheckedFunc;

/**
 * Lint caching decorator that calls defects method only once. Uses in memory storage for caching.
 *
 * @param <T> The type of entity to analyze
 * @since 0.0.42
 */
public class LtDfSticky<T> implements Lint<T> {

    private final Lint<T> origin;

    private final UncheckedFunc<T, Collection<Defect>> defectsFunc;

    public LtDfSticky(Lint<T> origin) {
        this.origin = origin;
        this.defectsFunc = new UncheckedFunc<>(new StickyFunc<>(origin::defects));
    }

    @Override
    public String name() {
        return origin.name();
    }

    @Override
    public Collection<Defect> defects(T entity) throws IOException {
        return this.defectsFunc.apply(entity);
    }

    @Override
    public String motive() throws IOException {
        return origin.motive();
    }
}
