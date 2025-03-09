package fixtures;

import java.io.IOException;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;
import org.eolang.lints.Defect;

/**
 * Fake class for Lint class.
 *
 * @since 0.0.42
 */
public final class LtFake<T> implements org.eolang.lints.Lint<T> {

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
     */
    public LtFake(
            Supplier<String> name,
            Function<T, Collection<Defect>> defects,
            Supplier<String> motive
    ) {
        this.name = name;
        this.defects = defects;
        this.motive = motive;
    }

    /**
     * Constructor. Name and motive are provided by value.
     */
    public LtFake(String name, Function<T, Collection<Defect>> defects, String motive) {
        this(() -> name, defects, () -> motive);
    }

    /**
     * Constructor. Name and motive are hardcoded.
     */
    public LtFake(Function<T, Collection<Defect>> defects) {
        this("Lint", defects, "Motive");
    }

    @Override
    public String name() {
        return this.name.get();
    }

    @Override
    public Collection<Defect> defects(T entity) throws IOException {
        return this.defects.apply(entity);
    }

    @Override
    public String motive() throws IOException {
        return this.motive.get();
    }
}
