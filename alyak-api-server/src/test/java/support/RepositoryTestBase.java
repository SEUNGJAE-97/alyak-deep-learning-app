package support;

import com.github.seungjae97.alyak.alyakapiserver.config.QuerydslConfig;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
@Import({QueryCounter.class, QuerydslConfig.class})
public abstract class RepositoryTestBase {

    @Autowired
    protected EntityManager entityManager;

    @BeforeEach
    void resetCounter() {
        QueryCounter.reset();
    }

    protected void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
        QueryCounter.reset();
    }

    protected void assertQueryCount(int expected) {
        int actual = QueryCounter.getCount();
        assertThat(actual)
                .withFailMessage("\n[Query Count Mismatch]\nExpected: %d\nActual: %d", expected, actual)
                .isEqualTo(expected);
    }

    protected void assertQueryCountAtMost(int max) {
        assertThat(QueryCounter.getCount())
                .withFailMessage("\n[Query Count Mismatch]\nMax: %d\nActual: %d", max, QueryCounter.getCount())
                .isLessThanOrEqualTo(max);
    }

    protected <T> void assertNotFound(Optional<T> result) {
        assertThat(result).isEmpty();
    }

    protected void assertDataIntegrityViolation(Supplier<?> action) {
        assertThatThrownBy(action::get)
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
