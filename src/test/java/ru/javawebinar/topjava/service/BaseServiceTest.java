package ru.javawebinar.topjava.service;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.Stopwatch;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.ActiveDbProfileResolver;

import java.util.concurrent.TimeUnit;

import static org.slf4j.LoggerFactory.getLogger;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@ActiveProfiles(resolver = ActiveDbProfileResolver.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public abstract class BaseServiceTest {
    private static final Logger log = getLogger("result");

    private static StringBuilder results;

    @Rule
    public Stopwatch measure = new Stopwatch() {
        protected void finished(long nanos, Description description) {
            String result = String.format("\n%-47s %7d", description.getTestClass().getSimpleName() + ":" + description.getMethodName(), TimeUnit.NANOSECONDS.toMillis(nanos));
            results.append(result);
            log.info(result + " ms\n");
        }
    };

    @BeforeClass
    public static void init() {
        results = new StringBuilder();
    }

    @AfterClass
    public static void overview() {
        printResult();
    }

    public static void printResult() {
        log.info("\n-------------------------------------------------------" +
                "\nTest                                       Duration, ms" +
                "\n-------------------------------------------------------" +
                results +
                "\n-------------------------------------------------------");
    }
}
