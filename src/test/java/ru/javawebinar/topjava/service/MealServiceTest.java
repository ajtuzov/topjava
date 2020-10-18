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
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
import static ru.javawebinar.topjava.UserTestData.USER_ID;
import static ru.javawebinar.topjava.util.Util.isBetweenHalfOpen;

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
        Meal newMeal = getNew();
        Meal created = mealService.create(newMeal, USER_ID);
        Integer id = created.getId();
        newMeal.setId(id);
        assertMatch(created, newMeal);
        assertMatch(mealService.get(id, USER_ID), newMeal);
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
        Meal meal = new Meal(FIRST_USER_MEAL);
        meal.setId(null);
        assertThrows(DataAccessException.class, () -> mealService.create(meal, USER_ID));
    }

    @Test
    public void get() {
        Meal actual = mealService.get(USER_MEAL_ID, USER_ID);
        assertMatch(actual, FIRST_USER_MEAL);
    }

    @Test
    public void getNotFound() {
        assertThrows(NotFoundException.class, () -> mealService.get(USER_MEAL_ID, ADMIN_ID));
    }

    @Test
    public void getBetweenInclusive() {
        LocalDate startDate = SECOND_ADMIN_MEAL.getDate();
        LocalDate endDate = FIRST_ADMIN_MEAL.getDate();
        List<Meal> actual = mealService.getBetweenInclusive(startDate, endDate, ADMIN_ID);
        List<Meal> collect = Stream.of(FIRST_ADMIN_MEAL, SECOND_ADMIN_MEAL, THIRD_ADMIN_MEAL)
                .filter(meal -> isBetweenHalfOpen(meal.getDate(), startDate, endDate.plusDays(1)))
                .sorted(comparing(Meal::getDateTime).reversed())
                .collect(toList());
        assertMatch(actual, collect);
    }

    @Test
    public void getAll() {
        List<Meal> actual = mealService.getAll(USER_ID);
        List<Meal> meals = Arrays.asList(FIRST_USER_MEAL, SECOND_USER_MEAL);
        meals.sort(comparing(Meal::getDateTime).reversed());
        MealTestData.assertMatch(actual, meals);
    }

    @Test
    public void update() {
        Meal updated = getUpdated();
        mealService.update(updated, USER_ID);
        assertMatch(mealService.get(USER_MEAL_ID, USER_ID), updated);
    }

    @Test
    public void updateNotFound() {
        Meal updated = getUpdated();
        assertThrows(NotFoundException.class, () -> mealService.update(updated, ADMIN_ID));
    }
}