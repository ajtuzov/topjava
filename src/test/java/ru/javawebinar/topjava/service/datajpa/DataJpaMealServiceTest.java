package ru.javawebinar.topjava.service.datajpa;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Stopwatch;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.MealServiceTest;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.slf4j.LoggerFactory.getLogger;
import static ru.javawebinar.topjava.MealTestData.MEAL1_ID;
import static ru.javawebinar.topjava.MealTestData.MEAL_MATCHER;
import static ru.javawebinar.topjava.MealTestData.meal1;
import static ru.javawebinar.topjava.Profiles.DATAJPA;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
import static ru.javawebinar.topjava.UserTestData.USER_ID;
import static ru.javawebinar.topjava.UserTestData.USER_MATCHER;
import static ru.javawebinar.topjava.UserTestData.user;

@ActiveProfiles(profiles = DATAJPA)
public class DataJpaMealServiceTest extends MealServiceTest {

    private static final Logger log = getLogger(DataJpaMealServiceTest.class);

    private static final StringBuilder results = new StringBuilder();

    @Rule
    public Stopwatch measure = new Stopwatch() {
        protected void finished(long nanos, Description description) {
            String result = String.format("\n%-46s %7d", description.getTestClass().getSimpleName() + ":" + description.getMethodName(), TimeUnit.NANOSECONDS.toMillis(nanos));
            results.append(result);
            log.info(result + " ms\n");
        }
    };

    @Test
    public void getWithUser() {
        Meal actual = service.getWithUser(MEAL1_ID, USER_ID);
        User actualUser = actual.getUser();
        MEAL_MATCHER.assertMatch(actual, meal1);
        USER_MATCHER.assertMatch(actualUser, user);
    }

    @Test
    public void getNotOwnWithUser() {
        assertThrows(NotFoundException.class, () -> service.getWithUser(MEAL1_ID, ADMIN_ID));
    }

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
