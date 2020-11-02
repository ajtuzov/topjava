package ru.javawebinar.topjava.service.datajpa;

import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.UserServiceTest;

import java.util.List;

import static ru.javawebinar.topjava.MealTestData.MEAL_MATCHER;
import static ru.javawebinar.topjava.MealTestData.meals;
import static ru.javawebinar.topjava.Profiles.DATAJPA;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

@ActiveProfiles(profiles = DATAJPA)
public class DataJPAUserServiceTest extends UserServiceTest {

    @Test
    public void getUserMeal() {
        List<Meal> actual = service.getUserMeal(USER_ID);
        MEAL_MATCHER.assertMatch(actual, meals);
    }
}
