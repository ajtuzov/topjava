package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static ru.javawebinar.topjava.Profiles.HSQL_DB;

@Repository
@Profile(HSQL_DB)
public class JdbcHsqldbMealRepository extends BaseJdbcMealRepository<Timestamp> {
    @Autowired
    public JdbcHsqldbMealRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(jdbcTemplate, namedParameterJdbcTemplate);
    }

    @Override
    public Timestamp getDateStrategy(LocalDateTime localDateTime) {
        return Timestamp.valueOf(localDateTime);
    }
}
