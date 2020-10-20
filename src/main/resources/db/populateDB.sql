DELETE FROM user_roles;
DELETE FROM meals;
DELETE FROM users;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (name, email, password)
VALUES ('User', 'user@yandex.ru', 'password'),
       ('Admin', 'admin@gmail.com', 'admin');

INSERT INTO user_roles (role, user_id)
VALUES ('USER', 100000),
       ('ADMIN', 100001);

INSERT INTO meals (date_time, description, calories, user_id)
VALUES ('2020-01-30 10:00:00', 'User: Завтрак', 500, 100000),
       ('2020-01-29 01:00:00', 'User: Ночной жор', 2100, 100000),
       ('2020-01-31 00:00:00', 'Admin: Передание', 2500, 100001),
       ('2020-01-30 20:00:00', 'Admin: Сыр', 600, 100001),
       ('2020-01-30 20:01:00', 'Admin: Вино', 400, 100001);