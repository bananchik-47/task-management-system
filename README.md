# Task Management System

REST API для управления задачами с аутентификацией на JWT. Каждый пользователь работает только со своими задачами.

## Описание

**Task Management System** — backend-приложение на Spring Boot, предоставляющее HTTP API для:

- регистрации и входа пользователей;
- получения профиля текущего пользователя;
- создания, чтения, обновления и удаления задач (CRUD);
- фильтрации задач по статусу и приоритету.

Архитектура: Controller → Service → Repository, маппинг через MapStruct, миграции схемы БД через Liquibase.

## Технологии

| Категория | Стек |
|-----------|------|
| Язык | Java 21 |
| Framework | Spring Boot 4.1.0 |
| Web | Spring Web MVC |
| Безопасность | Spring Security + JWT (JJWT 0.12.6) |
| БД | PostgreSQL 16, Spring Data JPA, Liquibase |
| Маппинг | MapStruct 1.6.0 |
| Утилиты | Lombok, Bean Validation |
| Сборка | Maven |
| Контейнеризация | Docker, Docker Compose |

## Требования

- **Локально без Docker:** JDK 21, Maven 3.9+ (или `./mvnw`), PostgreSQL 16
- **С Docker:** Docker 24+ и Docker Compose v2

## Быстрый старт (Docker)

При первом запуске PostgreSQL автоматически создаёт базу `task_management` (переменная `POSTGRES_DB`).
Контейнер `app` стартует только после успешного healthcheck PostgreSQL (`depends_on: service_healthy`).

```bash
# Сборка и запуск PostgreSQL + приложения
docker compose up --build

# В фоне
docker compose up --build -d

# Остановка
docker compose down

# Остановка с удалением данных БД (используйте, если БД не создалась или нужен чистый старт)
docker compose down -v
```

После старта API доступен по адресу: **http://localhost:8080**

PostgreSQL доступен на хосте по адресу **localhost:5433** (внутри Docker-сети контейнер `app` подключается к `postgres:5432`).

| Параметр | Значение (Docker) |
|----------|-------------------|
| Host (с машины) | `localhost:5433` |
| Host (между контейнерами) | `postgres:5432` |
| Database | `task_management` |
| Username | `postgres` |
| Password | `postgres` |

Liquibase автоматически применит миграции при первом запуске приложения.

### Устранение проблем с БД в Docker

Если база `task_management` не создалась (например, том PostgreSQL был инициализирован ранее без `POSTGRES_DB`):

```bash
docker compose down -v
docker compose up --build
```

Команда `-v` удаляет volume `postgres_data` и позволяет PostgreSQL заново инициализировать кластер с нужной базой.

## Запуск без Docker

### 1. Поднять PostgreSQL

```bash
# Пример: создать БД в локальном PostgreSQL
psql -U postgres -c "CREATE DATABASE task_management;"
```

### 2. Настроить подключение (при необходимости)

По умолчанию в `application.yml`:

| Параметр | Значение по умолчанию |
|----------|----------------------|
| URL | `jdbc:postgresql://localhost:5432/task_management` |
| Username | `postgres` |
| Password | `postgres` |

### 3. Собрать и запустить

```bash
./mvnw clean package
java -jar target/task-management-system-0.0.1-SNAPSHOT.jar
```

Или в режиме разработки:

```bash
./mvnw spring-boot:run
```

## Переменные окружения

| Переменная | Описание | По умолчанию |
|------------|----------|--------------|
| `SPRING_DATASOURCE_URL` | JDBC URL PostgreSQL | `jdbc:postgresql://localhost:5432/task_management` |
| `SPRING_DATASOURCE_USERNAME` | Имя пользователя БД | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Пароль БД | `postgres` |
| `SPRING_PROFILES_ACTIVE` | Активный профиль Spring (`docker` — для контейнера) | — |
| `SERVER_PORT` | Порт HTTP-сервера | `8080` |
| `JWT_SECRET` | Секрет для подписи JWT (минимум 64 символа для HS256) | см. `application.yml` |
| `JWT_EXPIRATION_MS` | Время жизни токена в миллисекундах | `86400000` (24 ч) |

> **Важно:** в production обязательно задайте собственный `JWT_SECRET` длиной не менее 64 символов.

## API — эндпоинты

Базовый URL: `http://localhost:8080`

Защищённые эндпоинты требуют заголовок:

```
Authorization: Bearer <ваш_jwt_токен>
```

### Аутентификация

| Метод | URL | Auth | Описание |
|-------|-----|------|----------|
| `POST` | `/auth/register` | Нет | Регистрация |
| `POST` | `/auth/login` | Нет | Вход |

### Пользователь

| Метод | URL | Auth | Описание |
|-------|-----|------|----------|
| `GET` | `/users/me` | Да | Текущий пользователь |

### Задачи

| Метод | URL | Auth | Описание |
|-------|-----|------|----------|
| `GET` | `/tasks` | Да | Список задач (фильтры: `status`, `priority`) |
| `GET` | `/tasks/{id}` | Да | Задача по ID |
| `POST` | `/tasks` | Да | Создать задачу |
| `PUT` | `/tasks/{id}` | Да | Обновить задачу |
| `DELETE` | `/tasks/{id}` | Да | Удалить задачу (204) |

**Статусы задачи:** `NEW`, `IN_PROGRESS`, `DONE`  
**Приоритеты:** `LOW`, `MEDIUM`, `HIGH`

---

## Примеры запросов (Postman / curl)

### 1. Регистрация

```http
POST /auth/register
Content-Type: application/json

{
  "username": "john",
  "password": "password123",
  "email": "john@example.com"
}
```

**Ответ (201):**

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### 2. Вход

```http
POST /auth/login
Content-Type: application/json

{
  "username": "john",
  "password": "password123"
}
```

**Ответ (200):**

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### 3. Текущий пользователь

```http
GET /users/me
Authorization: Bearer <token>
```

**Ответ (200):**

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "username": "john",
  "email": "john@example.com",
  "roles": ["ROLE_USER"]
}
```

### 4. Создать задачу

```http
POST /tasks
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Изучить Spring Security",
  "description": "Настроить JWT и фильтры",
  "priority": "HIGH"
}
```

**Ответ (201):**

```json
{
  "id": "660e8400-e29b-41d4-a716-446655440001",
  "title": "Изучить Spring Security",
  "description": "Настроить JWT и фильтры",
  "status": "NEW",
  "priority": "HIGH",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "createdAt": "2024-06-26T12:00:00Z",
  "updatedAt": "2024-06-26T12:00:00Z"
}
```

### 5. Список задач с фильтрами

```http
GET /tasks?status=NEW&priority=HIGH
Authorization: Bearer <token>
```

```http
GET /tasks?status=IN_PROGRESS
Authorization: Bearer <token>
```

### 6. Получить задачу по ID

```http
GET /tasks/660e8400-e29b-41d4-a716-446655440001
Authorization: Bearer <token>
```

### 7. Обновить задачу

```http
PUT /tasks/660e8400-e29b-41d4-a716-446655440001
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Изучить Spring Security — готово",
  "status": "DONE",
  "priority": "MEDIUM"
}
```

> В `PUT` все поля опциональны — обновляются только переданные значения.

### 8. Удалить задачу

```http
DELETE /tasks/660e8400-e29b-41d4-a716-446655440001
Authorization: Bearer <token>
```

**Ответ:** `204 No Content`

---

## Примеры curl

```bash
# Регистрация
curl -s -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"john","password":"password123","email":"john@example.com"}'

# Логин (сохранить токен)
TOKEN=$(curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john","password":"password123"}' | jq -r '.token')

# Создать задачу
curl -s -X POST http://localhost:8080/tasks \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"Моя задача","description":"Описание","priority":"MEDIUM"}'

# Список задач
curl -s http://localhost:8080/tasks \
  -H "Authorization: Bearer $TOKEN"
```

## Сборка JAR

```bash
./mvnw clean package
```

Артефакт: `target/task-management-system-0.0.1-SNAPSHOT.jar`

Запуск JAR:

```bash
java -jar target/task-management-system-0.0.1-SNAPSHOT.jar
```

## Структура проекта

```
src/main/java/com/example/taskmanagement/
├── config/          # Конфигурация (при необходимости)
├── controller/      # REST-контроллеры
├── dto/             # DTO и валидация
├── entity/          # JPA-сущности
├── exception/       # Обработка ошибок
├── mapper/          # MapStruct-мапперы
├── repository/      # Spring Data JPA
├── security/        # JWT, SecurityConfig
└── service/         # Бизнес-логика
```

## Коды ответов и ошибки

| Код | Ситуация |
|-----|----------|
| `400` | Ошибка валидации |
| `401` | Неверные учётные данные / отсутствует токен |
| `403` | Доступ запрещён |
| `404` | Пользователь или задача не найдены |
| `409` | Username или email уже заняты |
| `500` | Внутренняя ошибка сервера |

Формат ошибки:

```json
{
  "timestamp": "2024-06-26T12:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Task not found with id: ..."
}
```

## Лицензия

Учебный / демонстрационный проект.
