package ru.javawebinar.topjava.repository;

public class JpaUtil {

    public static String useCache = "true";

    public static void disable2ndLevelHibernateCache() {
        useCache = "false";
    }
}
