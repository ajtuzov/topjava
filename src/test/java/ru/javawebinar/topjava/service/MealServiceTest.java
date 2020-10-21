package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    static {
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService mealService;

    @Test
    public void create() {
        Meal expected = getNew();
        Meal actual = mealService.create(getNew(), USER_ID);
        Integer id = actual.getId();
        expected.setId(id);
        assertMatch(actual, expected);
        assertMatch(mealService.get(id, USER_ID), expected);
    }

    @Test
    public void delete() {
        mealService.delete(USER_MEAL_ID, USER_ID);
        assertThrows(NotFoundException.class, () -> mealService.get(USER_MEAL_ID, USER_ID));
    }

    @Test
    public void deleteNotFound() {
        assertThrows(NotFoundException.class, () -> mealService.delete(USER_MEAL_ID, ADMIN_ID));
    }

    @Test
    public void duplicateMealCreate() {
        Meal meal = new Meal(firstUserMeal);
        meal.setId(null);
        assertThrows(DataAccessException.class, () -> mealService.create(meal, USER_ID));
    }

    @Test
    public void get() {
        Meal actual = mealService.get(USER_MEAL_ID, USER_ID);
        assertMatch(actual, firstUserMeal);
    }

    @Test
    public void getNotFound() {
        assertThrows(NotFoundException.class, () -> mealService.get(USER_MEAL_ID, ADMIN_ID));
    }

    @Test
    public void getBetweenInclusive() {
        LocalDate startDate = secondAdminMeal.getDate();
        LocalDate endDate = thirdAdminMeal.getDate();
        List<Meal> actual = mealService.getBetweenInclusive(startDate, endDate, ADMIN_ID);
        List<Meal> expected = Arrays.asList(thirdAdminMeal, secondAdminMeal);
        assertMatch(actual, expected);
    }

    @Test
    public void getBetweenInclusiveWithoutBorders() {
        List<Meal> actual = mealService.getBetweenInclusive(null, null, ADMIN_ID);
        List<Meal> expected = Arrays.asList(firstAdminMeal, thirdAdminMeal, secondAdminMeal);
        assertMatch(actual, expected);
    }

    @Test
    public void getAll() {
        List<Meal> actual = mealService.getAll(USER_ID);
        List<Meal> expected = Arrays.asList(firstUserMeal, secondUserMeal);
        MealTestData.assertMatch(actual, expected);
    }

    @Test
    public void update() {
        Meal expected = getUpdated();
        mealService.update(getUpdated(), USER_ID);
        assertMatch(mealService.get(USER_MEAL_ID, USER_ID), expected);
    }

    @Test
    public void updateNotFound() {
        Meal updated = getUpdated();
        assertThrows(NotFoundException.class, () -> mealService.update(updated, ADMIN_ID));
    }

    @Test
    public void getNotExisting() {
        assertThrows(NotFoundException.class, () -> mealService.get(NOT_EXIST_MEAL_ID, USER_ID));
    }

    @Test
    public void deleteNotExisting() {
        assertThrows(NotFoundException.class, () -> mealService.delete(NOT_EXIST_MEAL_ID, USER_ID));
    }

    @Test
    public void updateNotExisting() {
        assertThrows(NotFoundException.class, () -> mealService.update(getNotExist(), USER_ID));
    }
}