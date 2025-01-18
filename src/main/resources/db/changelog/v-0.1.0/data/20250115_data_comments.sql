--liquibase formatted sql

--changeset deekul:20250115_data_comments context:local

INSERT INTO comments(id, text, created_date, user_id, task_id)

VALUES (1, 'Нужно продумать архитектуру поиска.', '2025-01-01 10:00:00', 3, 1),
       (2, 'Обратить внимание на масштабируемость.', '2025-01-02 14:30:15', 4, 1),
       (3, 'Обсудить выбор алгоритма на следующем митинге.', '2025-01-03 09:45:20', 5, 1),
       (4, 'Начать с анализа текущего модуля.', '2025-01-02 16:10:00', 4, 2),
       (5, 'Необходимо учесть возможные уязвимости.', '2025-01-03 11:05:50', 6, 2),
       (6, 'Подготовить тестовые данные.', '2025-01-03 17:00:00', 7, 3),
       (7, 'Проверить интеграцию с другими модулями.', '2025-01-04 13:20:30', 8, 3),
       (8, 'Проверить производительность', '2025-01-04 18:45:00', 9, 3),
       (9, 'Изучить документацию по Jenkins.', '2025-01-05 10:30:00', 8, 4),
       (10, 'Учесть требования к безопасности.', '2025-01-05 15:40:10', 2, 4),
       (11, 'Приложить скриншоты для лучшего понимания.', '2025-01-06 09:00:00', 7, 5),
       (12, 'Необходимо проверить, как выглядят отчеты на разных разрешениях', '2025-01-06 17:00:50', 5, 5),
       (13, 'Сравнить условия разных платежных систем.', '2025-01-07 11:20:00', 2, 6),
       (14, 'Провести тестирование после интеграции.', '2025-01-07 16:30:00', 3, 6),
       (15, 'Проверить зависимости библиотек.', '2025-01-08 12:45:00', 4, 7),
       (16, 'Учесть возможные конфликты версий', '2025-01-08 18:15:20', 5, 7),
       (17, 'Протестировать после обновления', '2025-01-09 10:00:00', 6, 7),
       (18, 'Предоставить примеры запросов в документации.', '2025-01-09 14:00:00', 9, 8),
       (19, 'Необходимо добавить диаграммы.', '2025-01-09 20:10:00', 8, 8),
       (20, 'Разработать программу обучения.', '2025-01-10 11:00:00', 3, 9),
       (21, 'Обеспечить обратную связь после обучения', '2025-01-11 14:20:00', 2, 9),
       (22, 'Проверить нагрузку при пиковых значениях', '2025-01-12 13:00:00', 4, 10),
       (23, 'Проверить базу данных.', '2025-01-14 19:00:00', 7, 10);