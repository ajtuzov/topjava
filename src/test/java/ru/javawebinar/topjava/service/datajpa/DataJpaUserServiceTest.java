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
import ru.javawebinar.topjava.service.UserServiceTest;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.slf4j.LoggerFactory.getLogger;
import static ru.javawebinar.topjava.MealTestData.MEAL_MATCHER;
import static ru.javawebinar.topjava.MealTestData.meals;
import static ru.javawebinar.topjava.Profiles.DATAJPA;
import static ru.javawebinar.topjava.UserTestData.USER_ID;
import static ru.javawebinar.topjava.UserTestData.USER_MATCHER;
import static ru.javawebinar.topjava.UserTestData.USER_WITHOUT_MEAL;
import static ru.javawebinar.topjava.UserTestData.user;

@ActiveProfiles(profiles = DATAJPA)
public class DataJpaUserServiceTest extends UserServiceTest {

    private static final Logger log = getLogger(DataJpaUserServiceTest.class);

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
    public void getWithMeals() {
        User actual = service.getWithMeals(USER_ID);
        List<Meal> actualMeals = actual.getMeals();
        USER_MATCHER.assertMatch(actual, user);
        MEAL_MATCHER.assertMatch(actualMeals, meals);
    }

    @Test
    public void getUserWhoHasNoMeal() {
        User withMeals = service.getWithMeals(USER_WITHOUT_MEAL);
        List<Meal> actual = withMeals.getMeals();
        Assert.assertEquals(0, actual.size());
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
