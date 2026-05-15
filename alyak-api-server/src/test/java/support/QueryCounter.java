package support;

import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.springframework.stereotype.Component;

@Component
public class QueryCounter implements StatementInspector {

    private static final ThreadLocal<Integer> count =
            ThreadLocal.withInitial(() -> 0);

    @Override
    public String inspect(String sql) {
        count.set(count.get() + 1);
        return sql;
    }

    public static int getCount() {
        return count.get();
    }

    public static void reset() {
        count.set(0);
    }
}