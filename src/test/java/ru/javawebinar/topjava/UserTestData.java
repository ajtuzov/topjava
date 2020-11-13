package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;

import java.util.Collections;
import java.util.Date;

import static java.util.EnumSet.allOf;
import static java.util.EnumSet.noneOf;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;
import static ru.javawebinar.topjava.model.Role.ADMIN;
import static ru.javawebinar.topjava.model.Role.USER;

public class UserTestData {
    public static TestMatcher<User> USER_MATCHER = TestMatcher.usingIgnoringFieldsComparator("registered", "meals");

    public static final int USER_ID = START_SEQ;
    public static final int ADMIN_ID = START_SEQ + 1;
    public static final int NOT_FOUND = 10;

    public static final User user = new User(USER_ID, "User", "user@yandex.ru", "password", USER);
    public static final User admin = new User(ADMIN_ID, "Admin", "admin@gmail.com", "admin", USER, ADMIN);

    public static User getNew() {
        return new User(null, "New", "new@gmail.com", "newPass", 1555, false, new Date(), Collections.singleton(USER));
    }

    public static User getNewWithoutRoles() {
        User user = getNew();
        user.setRoles(noneOf(Role.class));
        return user;
    }

    public static User getNewWithManyRoles() {
        User user = getNew();
        user.setRoles(allOf(Role.class));
        return user;
    }

    public static User getUpdated() {
        User updated = new User(user);
        updated.setEmail("update@gmail.com");
        updated.setName("UpdatedName");
        updated.setCaloriesPerDay(330);
        updated.setPassword("newPass");
        updated.setEnabled(false);
        updated.setRoles(Collections.singletonList(ADMIN));
        return updated;
    }

    public static User getUpdatedWithoutRoles() {
        User user = getUpdated();
        user.setRoles(noneOf(Role.class));
        return user;
    }

    public static User getUpdatedWithManyRoles() {
        User user = getUpdated();
        user.setRoles(allOf(Role.class));
        return user;
    }
}
