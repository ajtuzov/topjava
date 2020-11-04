package ru.javawebinar.topjava.service.datajpa;

import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.UserServiceTest;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static ru.javawebinar.topjava.MealTestData.MEAL_MATCHER;
import static ru.javawebinar.topjava.MealTestData.meals;
import static ru.javawebinar.topjava.Profiles.DATAJPA;
import static ru.javawebinar.topjava.UserTestData.USER_ID;
import static ru.javawebinar.topjava.UserTestData.USER_MATCHER;
import static ru.javawebinar.topjava.UserTestData.USER_WITHOUT_MEAL;
import static ru.javawebinar.topjava.UserTestData.user;
import static ru.javawebinar.topjava.UserTestData.userWithoutMeal;

@ActiveProfiles(profiles = DATAJPA)
public class DataJpaUserServiceTest extends UserServiceTest {

    @Test
    public void getWithMeals() {
        User actual = service.getWithMeals(USER_ID);
        List<Meal> actualMeals = actual.getMeals();
        USER_MATCHER.assertMatch(actual, user);
        MEAL_MATCHER.assertMatch(actualMeals, meals);
    }

    @Test
    public void getUserWhoHasNoMeal() {
        User actual = service.getWithMeals(USER_WITHOUT_MEAL);
        List<Meal> actualMeal = actual.getMeals();
        USER_MATCHER.assertMatch(actual, userWithoutMeal);
        assertEquals(0, actualMeal.size());
    }
}
