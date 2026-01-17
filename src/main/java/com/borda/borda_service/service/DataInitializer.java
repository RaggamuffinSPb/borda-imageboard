package com.borda.borda_service.service;

import com.borda.borda_service.entity.BoardTask;
import com.borda.borda_service.repository.BoardTaskRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

//@Component // Говорим Spring'у: "Создай этот бин при старте"
public class DataInitializer implements CommandLineRunner {

    private final BoardTaskRepository repository;

    // Внедряем зависимость (автоматически подставит репозиторий)
    public DataInitializer(BoardTaskRepository repository) {
        this.repository = repository;
    }

    @Override
    @SuppressWarnings("nullness") // Говорим IntelliJ: "Я знаю, что делаю, замолчи"
    public void run(String... args) throws Exception {
        // Этот код выполнится сразу после запуска приложения

        // 1. Создаем задачу в памяти Java
        BoardTask task = new BoardTask();
        task.setTitle("Первая задача из Java");
        task.setDescription("Проверяем, как работает сохранение");
        task.setDone(false);

        // 2. Сохраняем в базу через репозиторий
        repository.save(task);

        System.out.println("Я сохранил задачу! Проверь pgAdmin!");
    }
}