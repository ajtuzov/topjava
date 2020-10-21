package ru.javawebinar.topjava;

import org.assertj.core.api.Assertions;
import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;

import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class MealTestData {

    public static final int USER_MEAL_ID = START_SEQ + 2;
    public static final int ADMIN_MEAL_ID = START_SEQ + 4;
    public static final int NOT_EXIST_MEAL_ID = 10;
    public static final Meal firstUserMeal = new Meal(USER_MEAL_ID, LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "User: Завтрак", 500);
    public static final Meal secondUserMeal = new Meal(USER_MEAL_ID + 1, LocalDateTime.of(2020, Month.JANUARY, 29, 1, 0), "User: Ночной жор", 2100);
    public static final Meal firstAdminMeal = new Meal(ADMIN_MEAL_ID, LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Admin: Передание", 2500);
    public static final Meal secondAdminMeal = new Meal(ADMIN_MEAL_ID + 1, LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Admin: Сыр", 600);
    public static final Meal thirdAdminMeal = new Meal(ADMIN_MEAL_ID + 2, LocalDateTime.of(2020, Month.JANUARY, 30, 20, 1), "Admin: Вино", 400);

    public static Meal getNew() {
        return new Meal(null, LocalDateTime.of(2020, Month.OCTOBER, 17, 12, 0), "Еда", 500);
    }

    public static Meal getUpdated() {
        Meal meal = new Meal(firstUserMeal);
        meal.setDescription("Перекус");
        meal.setCalories(800);
        meal.setDateTime(LocalDateTime.of(2020, Month.MARCH, 13, 0, 0));
        return meal;
    }

    public static Meal getNotExist() {
        return new Meal(NOT_EXIST_MEAL_ID, LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0), "Мясо единорога", 9000);
    }

    public static void assertMatch(Meal actual, Meal expected) {
        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Iterable<Meal> expected) {
        Assertions.assertThat(actual)
                .usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(expected);
    }
}
