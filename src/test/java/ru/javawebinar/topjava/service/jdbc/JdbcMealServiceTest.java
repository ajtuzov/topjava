package ru.javawebinar.topjava.service.jdbc;

import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.rules.Stopwatch;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.service.MealServiceTest;

import java.util.concurrent.TimeUnit;

import static org.slf4j.LoggerFactory.getLogger;
import static ru.javawebinar.topjava.Profiles.JDBC;

@ActiveProfiles(profiles = JDBC)
public class JdbcMealServiceTest extends MealServiceTest {

    private static final Logger log = getLogger(JdbcMealServiceTest.class);

    private static final StringBuilder results = new StringBuilder();

    @Rule
    public Stopwatch measure = new Stopwatch() {
        protected void finished(long nanos, Description description) {
            String result = String.format("\n%-46s %7d", description.getTestClass().getSimpleName() + ":" + description.getMethodName(), TimeUnit.NANOSECONDS.toMillis(nanos));
            results.append(result);
            log.info(result + " ms\n");
        }
    };

    @AfterClass
    public static void overview() {
        printResult();
    }

    public static void printResult() {
        log.info("\n------------------------------------------------------" +
                "\nTest                                      Duration, ms" +
                "\n------------------------------------------------------" +
                results +
                "\n------------------------------------------------------");
    }
}
