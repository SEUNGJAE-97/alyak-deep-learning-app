package support;

import com.github.seungjae97.alyak.alyakapiserver.config.QuerydslConfig;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import({QueryCounter.class, QuerydslConfig.class})
public abstract class RepositoryTestBase {

    @BeforeEach
    void resetCounter() {
        QueryCounter.reset();
    }

    protected void assertQueryCount(int expected) {
        int actual = QueryCounter.getCount();
        assertThat(actual)
                .withFailMessage("\n[Query Count Mismatch]\nExpected: %d\nActual: %d", expected, actual)
                .isEqualTo(expected);
    }
}