package ru.javawebinar.topjava;

public class Profiles {
    public static final String
            JPA = "jpa",
            DATAJPA = "datajpa",
            JDBC_POSTGRES = "jdbcPostgres",
            JDBC_HSQLDB = "jdbcHsqldb";

    public static final String REPOSITORY_IMPLEMENTATION = DATAJPA;

    public static final String
            POSTGRES_DB = "postgres",
            HSQL_DB = "hsqldb";

    //  Get DB profile depending of DB driver in classpath
    public static String getActiveDbProfile() {
        try {
            Class.forName("org.postgresql.Driver");
            return POSTGRES_DB;
        } catch (ClassNotFoundException ex) {
            try {
                Class.forName("org.hsqldb.jdbcDriver");
                return Profiles.HSQL_DB;
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("Could not find DB driver");
            }
        }
    }

    public static String getActiveJdbcProfile() {
        return getActiveDbProfile().equals(POSTGRES_DB) ? JDBC_POSTGRES : JDBC_HSQLDB;
    }
}
