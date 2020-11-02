package ru.javawebinar.topjava.service.jdbc;

import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.ActiveJdbcProfilesResolver;
import ru.javawebinar.topjava.service.UserServiceTest;

@ActiveProfiles(resolver = ActiveJdbcProfilesResolver.class)
public class JDBCUserServiceTest extends UserServiceTest {

}
