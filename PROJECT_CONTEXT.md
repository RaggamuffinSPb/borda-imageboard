# Project Context: Borda (Imageboard)
## Стек технологий
Java: 21  
Build Tool: Maven  
Framework: Spring Boot (Web, Data JPA)  
Database: PostgreSQL 15 (в Docker, порт 5435)  
Cache: Redis 7 (в Docker, порт 6379) - ПОКА НЕ ИСПОЛЬЗУЕТСЯ  
ORM: Hibernate  
Utils: Lombok  
IDE: IntelliJ IDEA Ultimate  
## Что уже сделано
Docker: Поднимаются контейнеры borda_postgres и borda_redis через docker-compose.yml.  
Database: Подключение настроено в application.yml.  
Entity: Есть сущность BoardTask (id, title, description, done). Таблица tasks в БД.  
Repository: BoardTaskRepository (JpaRepository).  
Controller: BoardTaskController (REST API).  
GET /tasks (все)  
POST /tasks (создать)  
GET /tasks/{id} (один)  
PUT /tasks/{id} (обновить)  
DELETE /tasks/{id} (удалить)  
## Текущая цель  
Переделать проект из "Менеджера задач" в "Имиджборду".  

Нужно уметь загружать картинки.  
Нужно хранить путь к картинке в БД.  
Отображать ленту картинок.  
Важные правила  
Пакеты пишутся с маленькой буквы (com.borda.borda_service.entity).  
Таблицы в snake_case и во множественном числе (tasks).  
Используем Lombok (@Data) для компактности.  
Как пользоваться в следующей сессии:  
Когда откроешь новый чат, напиши:  
"Привет! У меня есть проект Borda. Вот контекст из файла PROJECT_CONTEXT.md:" -> и вставь этот текст. ИИ мгновенно в курсе дел.  

