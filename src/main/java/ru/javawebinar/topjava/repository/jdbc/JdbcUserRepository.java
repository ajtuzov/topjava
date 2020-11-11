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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.sql.Types.INTEGER;
import static java.sql.Types.VARCHAR;
import static java.util.Collections.nCopies;
import static java.util.stream.Collectors.toList;
import static ru.javawebinar.topjava.util.ValidationUtil.validateEntity;

@Repository
@Transactional(readOnly = true)
public class JdbcUserRepository implements UserRepository {

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;

    private PlatformTransactionManager transactionManager;

    @Autowired
    public JdbcUserRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;

    }

    @Autowired
    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    @Transactional
    public User save(User user) {
        validateEntity(user);
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);
        TransactionDefinition definition = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(definition);
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
        transactionManager.commit(status);
        return user;
    }

    @Override
    @Transactional
    public boolean delete(int id) {
        TransactionDefinition definition = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(definition);
        int update = jdbcTemplate.update("DELETE FROM users WHERE id=?", id);
        transactionManager.commit(status);
        return update != 0;
    }

    @Override
    public User get(int id) {
        TransactionDefinition definition = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(definition);
        List<User> query = jdbcTemplate.query("""
                                SELECT *
                                FROM users
                                JOIN user_roles ur ON users.id = ur.user_id
                                WHERE users.id=?
                """, new UserWithRolesExtractor(), id);
        transactionManager.commit(status);
        return DataAccessUtils.singleResult(query);
    }

    @Override
    public User getByEmail(String email) {
        TransactionDefinition definition = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(definition);
        List<User> users = jdbcTemplate.query("""
                SELECT * 
                FROM users 
                JOIN user_roles ur ON users.id = ur.user_id
                WHERE email=?
                """, new UserWithRolesExtractor(), email);
        transactionManager.commit(status);
        return DataAccessUtils.singleResult(users);
    }

    @Override
    public List<User> getAll() {
        TransactionDefinition definition = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(definition);
        List<User> query = jdbcTemplate.query("""
                SELECT * 
                FROM users
                JOIN user_roles ur ON users.id = ur.user_id 
                ORDER BY name, email""", new UserWithRolesExtractor());
        transactionManager.commit(status);
        return query;
    }

    private void insertRoles(final User user) {
        int[] argTypes = {VARCHAR, INTEGER};
        List<Object[]> batchArgs = user.getRoles()
                .stream()
                .map(Role::toString)
                .map(str -> new Object[]{str, user.getId()})
                .collect(toList());
        jdbcTemplate.batchUpdate("""
                INSERT INTO user_roles (role, user_id)
                VALUES (?, ?)
                """, batchArgs, argTypes);
    }

    private void updateRoles(final User user) {
        int size = user.getRoles().size();
        List<Object[]> batchArgs = nCopies(size, new Object[]{user.getId()});
        jdbcTemplate.batchUpdate("""
                DELETE FROM user_roles
                WHERE user_id = ?
                """, batchArgs);
        insertRoles(user);
    }

    private static class UserWithRolesExtractor implements ResultSetExtractor<ArrayList<User>> {

        @Override
        public ArrayList<User> extractData(ResultSet rs) throws SQLException, DataAccessException {
            Map<Integer, User> map = new HashMap<>();
            User user;
            while (rs.next()) {
                int id = rs.getInt("id");
                user = map.get(id);
                if (user == null) {
                    user = new User();
                    user.setId(id);
                    user.setName(rs.getString("name"));
                    user.setPassword(rs.getString("password"));
                    user.setEmail(rs.getString("email"));
                    user.setEnabled(rs.getBoolean("enabled"));
                    user.setCaloriesPerDay(rs.getInt("calories_per_day"));
                    user.setRegistered(rs.getDate("registered"));
                    user.setRoles(new ArrayList<>());
                    map.put(id, user);
                }

                String role = rs.getString("role");
                user.getRoles().add(Role.valueOf(role));
            }
            return new ArrayList<>(map.values());
        }
    }
}
