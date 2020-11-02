package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
@Profile("jdbcPostgres")
public class JdbcPostgresMealRepository extends BaseJdbcMealRepository<LocalDateTime> {

    public JdbcPostgresMealRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(jdbcTemplate, namedParameterJdbcTemplate);
    }

    @Override
    public String[] getColumnNameStrategy() {
        return new String[] { "id" };
    }

    @Override
    public LocalDateTime getDateStrategy(LocalDateTime localDateTime) {
        return localDateTime;
    }

}
