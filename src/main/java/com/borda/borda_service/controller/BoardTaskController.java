package com.borda.borda_service.controller;

import com.borda.borda_service.entity.BoardTask;
import com.borda.borda_service.repository.BoardTaskRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Говорим Spring: "Это контроллер, он умеет отвечать на HTTP запросы"
@RequestMapping("/tasks") // Все запросы начнутся с /tasks
public class BoardTaskController {

    private final BoardTaskRepository repository;

    // Внедряем репозиторий (чтобы иметь доступ к базе)
    public BoardTaskController(BoardTaskRepository repository) {
        this.repository = repository;
    }

    // 1. Получить все задачи (GET запрос)
    // Обращение: GET http://localhost:8080/tasks
    @GetMapping
    public List<BoardTask> getAllTasks() {
        return repository.findAll(); // Hibernate сам сделает SELECT *
    }

    // 2. Создать задачу (POST запрос)
    // Обращение: POST http://localhost:8080/tasks
    @PostMapping
    public BoardTask createTask(@RequestBody BoardTask task) {
        // Мы получаем JSON в теле запроса, Spring превращает его в объект BoardTask
        return repository.save(task); // Hibernate сделает INSERT
    }

    // 3. Получить одну задачу по ID
    // Обращение: GET http://localhost:8080/tasks/1
    @GetMapping("/{id}")
    public ResponseEntity<BoardTask> getTaskById(@PathVariable Long id) {
        // findById возвращает "Опциональный" объект (может быть, а может не быть)
        return repository.findById(id)
                .map(task -> ResponseEntity.ok().body(task)) // Если нашел - верни задачу и статус 200 OK
                .orElse(ResponseEntity.notFound().build()); // Если не нашел - верни статус 404 Not Found
    }

    // 4. Удалить задачу по ID
    // Обращение: DELETE http://localhost:8080/tasks/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        // Проверяем, есть ли задача
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.noContent().build(); // Статус 204 No Content (все ок, тела нет)
        } else {
            return ResponseEntity.notFound().build(); // Статус 404
        }
    }

    // 5. Обновить задачу
    // Обращение: PUT http://localhost:8080/tasks/2
    @PutMapping("/{id}")
    public ResponseEntity<BoardTask> updateTask(@PathVariable Long id, @RequestBody BoardTask details) {
        // Ищем задачу по ID из URL
        return repository.findById(id)
                .map(existingTask -> {
                    // Если нашли — перезаписываем поля данными из запроса (details)
                    existingTask.setTitle(details.getTitle());
                    existingTask.setDescription(details.getDescription());
                    existingTask.setDone(details.isDone());

                    // Сохраняем (Hibernate сам поймет, что это UPDATE, а не INSERT, потому что ID есть)
                    return ResponseEntity.ok().body(repository.save(existingTask));
                })
                .orElse(ResponseEntity.notFound().build()); // Если не нашли — 404
    }
}