package ru.javawebinar.topjava.service.jdbc;

import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.ActiveJdbcProfilesResolver;
import ru.javawebinar.topjava.service.MealServiceTest;

@ActiveProfiles(resolver = ActiveJdbcProfilesResolver.class)
public class JDBCMealServiceTest extends MealServiceTest {

}
