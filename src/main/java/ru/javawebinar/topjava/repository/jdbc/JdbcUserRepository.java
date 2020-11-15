package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static java.sql.Types.INTEGER;
import static java.sql.Types.VARCHAR;
import static java.util.EnumSet.noneOf;
import static java.util.stream.Collectors.toList;
import static ru.javawebinar.topjava.util.ValidationUtil.validateEntity;

@Repository
@Transactional(readOnly = true)
public class JdbcUserRepository implements UserRepository {

    private final ResultSetExtractor<List<User>> extractor = new UserWithRolesExtractor();

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;

    @Autowired
    public JdbcUserRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    @Transactional
    public User save(User user) {
        validateEntity(user);
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);
        if (user.isNew()) {
            Number newKey = insertUser.executeAndReturnKey(parameterSource);
            int userId = newKey.intValue();
            user.setId(userId);
            insertRoles(user);
        } else {
            int update = namedParameterJdbcTemplate.update("""
                       UPDATE users SET name=:name, email=:email, password=:password, 
                       registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay WHERE id=:id
                    """, parameterSource);
            if (update == 0) {
                return null;
            }
            updateRoles(user);
        }
        return user;
    }

    private void insertRoles(final User user) {
        Set<Role> roles = Optional.ofNullable(user.getRoles())
                .orElse(noneOf(Role.class));

        List<Object[]> batchArgs = roles.stream()
                .map(Role::name)
                .map(str -> new Object[]{str, user.getId()})
                .collect(toList());

        int[] argTypes = {VARCHAR, INTEGER};
        jdbcTemplate.batchUpdate("""
                INSERT INTO user_roles (role, user_id)
                VALUES (?, ?)
                """, batchArgs, argTypes);
    }

    private void updateRoles(final User user) {
        jdbcTemplate.update("DELETE FROM user_roles WHERE user_id=?", user.getId());
        insertRoles(user);
    }

    @Override
    @Transactional
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
    }

    @Override
    public User get(int id) {
        List<User> query = jdbcTemplate.query("""
                SELECT *
                FROM users
                LEFT JOIN user_roles ur ON users.id = ur.user_id
                WHERE users.id=?
                """, extractor, id);
        return DataAccessUtils.singleResult(query);
    }

    @Override
    public User getByEmail(String email) {
        List<User> users = jdbcTemplate.query("""
                SELECT * 
                FROM users 
                LEFT JOIN user_roles ur ON users.id = ur.user_id
                WHERE email=?
                """, extractor, email);
        return DataAccessUtils.singleResult(users);
    }

    @Override
    public List<User> getAll() {
        return jdbcTemplate.query("""
                SELECT * 
                FROM users
                LEFT JOIN user_roles ur ON users.id = ur.user_id 
                ORDER BY name, email""", extractor);
    }

    private static class UserWithRolesExtractor implements ResultSetExtractor<List<User>> {

        @Override
        public List<User> extractData(ResultSet rs) throws SQLException, DataAccessException {
            Map<Integer, User> map = new LinkedHashMap<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                User user = map.computeIfAbsent(id, integer -> new User());
                Set<Role> roles = Optional.ofNullable(user.getRoles())
                        .orElse(noneOf(Role.class));
                if (user.isNew()) {
                    user.setId(id);
                    user.setName(rs.getString("name"));
                    user.setPassword(rs.getString("password"));
                    user.setEmail(rs.getString("email"));
                    user.setEnabled(rs.getBoolean("enabled"));
                    user.setCaloriesPerDay(rs.getInt("calories_per_day"));
                    user.setRegistered(rs.getDate("registered"));
                }

                Optional.ofNullable(rs.getString("role"))
                        .map(Role::valueOf)
                        .ifPresent(roles::add);
                user.setRoles(roles);
            }
            return new ArrayList<>(map.values());
        }
    }
}
