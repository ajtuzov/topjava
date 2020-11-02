package ru.javawebinar.topjava;

import org.springframework.test.context.ActiveProfilesResolver;

public class ActiveJdbcProfilesResolver implements ActiveProfilesResolver {
    @Override
    public String[] resolve(Class<?> testClass) {
        return new String[]{Profiles.getActiveJdbcProfile()};
    }
}
