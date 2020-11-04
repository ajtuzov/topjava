package ru.javawebinar.topjava.service.datajpa;

import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.MealServiceTest;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import static org.junit.Assert.assertThrows;
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
}
