# Инструкция по локальному запуску приложения

Описывает, как запустить приложение локально с помощью Docker Compose.

## Подготовка к запуску

Перед тем как запустить приложение, вам потребуется:

1.  **Установить Docker:**
    *   Если вы работаете на macOS или Windows, [скачайте и установите Docker Desktop](https://www.docker.com/products/docker-desktop). Это наиболее удобный способ для этих систем.
    *   Если вы используете Linux, установите [Docker Engine](https://docs.docker.com/engine/install/) и [Docker Compose](https://docs.docker.com/compose/install/) в соответствии с инструкцией.

2.  **Собрать проект с помощью Gradle:**
    *   Перед запуском с помощью Docker Compose, вам нужно собрать проект. В корневой директории проекта (где находится `build.gradle`) выполните следующую команду:

        ```bash
        ./gradlew build
        ```
        Эта команда соберет ваш проект и создаст JAR-файл(ы) в директории `build/libs/`.
    
3.  **Создать файл `.env`:**
    В той же папке, где находится `docker-compose.yml`, создайте файл с именем `.env` и заполните его переменными окружения.

    <details>
      <summary><b>Пример .env файла</b></summary>

    - SERVER_PORT=8080
    - POSTGRES_PORT_MAPPING=5433:5432
    - POSTGRES_USERNAME=user
    - POSTGRES_PASSWORD=your_secure_password
    - POSTGRES_DATABASE=user_service_db
    - POSTGRES_URL=jdbc:postgresql://postgresql:5432/user_service_db
    - JWT_SECRET_KEY=your_secret_jwt_key
    </details>
    
*   Замените `your_secure_password`, `user_service_db`, и `your_secret_jwt_key` на свои реальные значения.
*   `POSTGRES_PORT_MAPPING` определяет порт на вашем хосте (`5433`), который будет перенаправлен на порт `5432` внутри контейнера PostgreSQL. 
*   `SERVER_PORT` определяет порт, на котором будет работать ваше приложение.
*   Убедитесь, что `POSTGRES_URL` указывает на имя сервиса `postgresql` (это имя хоста для Docker контейнеров).

## Запуск приложения

1.  **Откройте терминал:**
    Перейдите в терминале в папку, где находятся файл `docker-compose.yml` и файл `.env`.

2.  **Запустите приложение:**
    Используйте следующую команду для запуска приложения в фоновом режиме:

    ```bash
    docker compose up -d
    ```

    *   `docker compose up`: Эта команда прочтет ваш `docker-compose.yml`, соберет необходимые образы и запустит контейнеры.
    *   `-d`:  Этот флаг запускает контейнеры в фоновом режиме.

3.  **Проверьте статус контейнеров:**
    После запуска, проверьте статус контейнеров:

    ```bash
    docker compose ps
    ```

    Вы должны увидеть, что контейнеры `user-service` и `postgresql` запущены.

4.  **Просмотр логов:**
    Для просмотра логов используйте:

    ```bash
    docker compose logs
    ```
    Для просмотра логов определенного контейнера:

    ```bash
    docker compose logs <имя контейнера>
     ```
5.  **Остановка приложения:**
    Для остановки приложения:

    ```bash
    docker compose down
    ```
    Эта команда остановит и удалит запущенные контейнеры.


