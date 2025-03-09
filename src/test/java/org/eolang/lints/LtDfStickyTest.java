package org.eolang.lints;

import fixtures.LtFake;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link PkWpa}.
 *
 * @since 0.0.42
 */
public class LtDfStickyTest {

    @Test
    void loadsDefectsOnlyOnce() throws IOException {
        AtomicInteger count = new AtomicInteger();
        Lint<Integer> lint = new LtDfSticky<>(
                new LtFake<>(entity -> {
                    count.set(count.get() + 1);
                    return Collections.emptyList();
                })
        );
        lint.defects(0);
        lint.defects(0);
        lint.defects(0);
        MatcherAssert.assertThat(
                count.get(),
                Matchers.equalTo(1)
        );
    }
}
