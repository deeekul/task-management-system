--liquibase formatted sql

--changeset deekul:20250114_data_users context:local

-- users data

INSERT INTO users(id, first_name, last_name, login, password,  role)

VALUES (1, 'Иванов', 'Иван', 'ivanov_i', '$2a$10$7wrtwctbL63wGqxCZvRv6ulQVkHrAgdHNiP3USKFE9V7Tj9kHnqC2', 'ADMIN'),
       (2, 'Малышев', 'Александр', 'malyshev_a', '$2a$10$KoT4Ctc7aheAEmBZFDH0TOQVOSNtm08qNkzm41pUqJREpm4xSYicS', 'USER'),
       (3, 'Бирюков', 'Сергей', 'biryukov_s', '$2a$10$19XjGQXNHI7/lqWTnQzBcex5jDYX5DuXsMI1YE0u0HR9.qLmcz4zi', 'USER'),
       (4, 'Котов', 'Алексей', 'kotov_a', '$2a$10$ESapxouT2CdmyGfvdINUuOzgoxnKtsyQYcsLdRbgUPb01JvugINPm', 'USER'),
       (5, 'Петров', 'Семён', 'petrov_s', '$2a$10$542HhF8LhKCtSqF.jtZfV.pkTKoZmsuuhIJhRADIEoeobdQx18GCi', 'USER'),
       (6, 'Сидоров', 'Артём', 'sidorov_a', '$2a$10$Hl7xHogi7kW5bB4WhvNzOu5Yh7df0wgZKR39ZB7/w5.qL9k/9OSHG', 'USER'),
       (7, 'Новиков', 'Денис', 'novikov_d', '$2a$10$3K1mG3U9bU8K1Z8d9xU3se5gI6OC8pol8MI8tneF1KbwmdHD9p3DG', 'USER'),
       (8, 'Смирнов', 'Александр', 'smirnov_a', '$2a$10$LPoOawtJ9ZldNHBe1SR0E.54aJ1Z0w8Lg.1QS0qk8e07aVc.AYt7e', 'USER'),
       (9, 'Фёдоров', 'Павел', 'fedorov_p', '$2a$10$YvDFn45Xz4PdYtDqvdd5XOYKoS8gt5qC45C.mZH7D0DpFZoBuQ3uO', 'USER'),
       (10, 'Кузнецов', 'Виктор', 'kuznetsov_v', '$2a$10$OlxHsBfrGvKZDIyY.bY2/OB6lzM8Lx.kK7NeVCTDEpBUg8fFfd4G6', 'ADMIN');