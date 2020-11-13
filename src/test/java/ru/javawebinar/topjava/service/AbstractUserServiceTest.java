package ru.javawebinar.topjava.service;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.UserTestData;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.JpaUtil;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import javax.validation.ConstraintViolationException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.Profiles.NO_CACHE;
import static ru.javawebinar.topjava.UserTestData.*;

@ActiveProfiles(NO_CACHE)
public abstract class AbstractUserServiceTest extends AbstractServiceTest {

    @Autowired
    protected UserService service;

    @BeforeClass
    public static void setUp() {
        JpaUtil.disable2ndLevelHibernateCache();
    }

    @Test
    public void create() {
        User created = service.create(getNew());
        int newId = created.id();
        User newUser = getNew();
        newUser.setId(newId);
        USER_MATCHER.assertMatch(created, newUser);
        USER_MATCHER.assertMatch(service.get(newId), newUser);
    }

    @Test
    public void createWithoutRoles() {
        User actual = service.create(getNewWithoutRoles());
        int newId = actual.id();
        User newUser = getNewWithoutRoles();
        newUser.setId(newId);
        USER_MATCHER.assertMatch(actual, newUser);
        USER_MATCHER.assertMatch(service.get(newId), newUser);
    }

    @Test
    public void createWithManyRoles() {
        User actual = service.create(getNewWithManyRoles());
        int newId = actual.id();
        User newUser = getNewWithManyRoles();
        newUser.setId(newId);
        USER_MATCHER.assertMatch(actual, newUser);
        USER_MATCHER.assertMatch(service.get(newId), newUser);
    }

    @Test
    public void createWithManyRolesAndCompareWithAll() {
        User newUser = service.create(getNewWithManyRoles());
        int newId = newUser.id();
        newUser.setId(newId);
        USER_MATCHER.assertMatch(service.getAll(), admin, newUser, user);
    }

    @Test
    public void duplicateMailCreate() {
        assertThrows(DataAccessException.class, () ->
                service.create(new User(null, "Duplicate", "user@yandex.ru", "newPass", Role.USER)));
    }

    @Test
    public void delete() {
        service.delete(USER_ID);
        assertThrows(NotFoundException.class, () -> service.get(USER_ID));
    }

    @Test
    public void deletedNotFound() {
        assertThrows(NotFoundException.class, () -> service.delete(NOT_FOUND));
    }

    @Test
    public void get() {
        User user = service.get(USER_ID);
        USER_MATCHER.assertMatch(user, UserTestData.user);
    }

    @Test
    public void getNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(NOT_FOUND));
    }

    @Test
    public void getByEmail() {
        User actual = service.getByEmail("user@yandex.ru");
        USER_MATCHER.assertMatch(actual, user);
    }

    @Test
    public void getByEmailWithManyRoles() {
        User actual = service.getByEmail("admin@gmail.com");
        USER_MATCHER.assertMatch(actual, admin);
    }

    @Test
    public void update() {
        User updated = getUpdated();
        service.update(updated);
        USER_MATCHER.assertMatch(service.get(USER_ID), getUpdated());
    }

    @Test
    public void updateWithManyRoles() {
        User updated = getUpdatedWithManyRoles();
        service.update(updated);
        USER_MATCHER.assertMatch(service.get(USER_ID), getUpdatedWithManyRoles());
    }

    @Test
    public void updateWithoutRoles() {
        User updated = getUpdatedWithoutRoles();
        service.update(updated);
        USER_MATCHER.assertMatch(service.get(USER_ID), getUpdatedWithoutRoles());
    }

    @Test
    public void getAll() {
        List<User> all = service.getAll();
        USER_MATCHER.assertMatch(all, admin, user);
    }

    @Test
    public void createWithException() {
        validateRootCause(() -> service.create(new User(null, "  ", "mail@yandex.ru", "password", Role.USER)), ConstraintViolationException.class);
        validateRootCause(() -> service.create(new User(null, "User", "  ", "password", Role.USER)), ConstraintViolationException.class);
        validateRootCause(() -> service.create(new User(null, "User", "mail@yandex.ru", "  ", Role.USER)), ConstraintViolationException.class);
        validateRootCause(() -> service.create(new User(null, "User", "mail@yandex.ru", "password", 9, true, new Date(), Set.of())), ConstraintViolationException.class);
        validateRootCause(() -> service.create(new User(null, "User", "mail@yandex.ru", "password", 10001, true, new Date(), Set.of())), ConstraintViolationException.class);
    }
}