package ru.javawebinar.topjava;

import org.assertj.core.api.Assertions;
import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;

import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class MealTestData {

    public static final int USER_MEAL_ID = START_SEQ + 2;
    public static final Meal FIRST_USER_MEAL = new Meal(100002, LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "User: Завтрак", 500);
    public static final Meal SECOND_USER_MEAL = new Meal(100003, LocalDateTime.of(2020, Month.JANUARY, 29, 1, 0), "User: Ночной жор", 2100);
    public static final Meal FIRST_ADMIN_MEAL = new Meal(100004, LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Admin: Передание", 2500);
    public static final Meal SECOND_ADMIN_MEAL = new Meal(100005, LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Admin: Сыр", 600);
    public static final Meal THIRD_ADMIN_MEAL = new Meal(100006, LocalDateTime.of(2020, Month.JANUARY, 30, 20, 1), "Admin: Вино", 400);


    public static Meal getNew() {
        return new Meal(null, LocalDateTime.of(2020, Month.OCTOBER, 17, 12, 0), "Еда", 500);
    }

    public static Meal getUpdated() {
        Meal meal = new Meal(FIRST_USER_MEAL);
        meal.setDescription("Перекус");
        meal.setCalories(800);
        return meal;
    }

    public static void assertMatch(Meal actual, Meal expected) {
        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Iterable<Meal> expected) {
        Assertions.assertThat(actual).isEqualTo(expected);
    }
}
