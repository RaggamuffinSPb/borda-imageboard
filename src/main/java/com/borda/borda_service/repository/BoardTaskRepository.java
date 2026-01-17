package com.borda.borda_service.repository;

import com.borda.borda_service.entity.BoardTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// JpaRepository<ТипСущности, ТипID>
@Repository
public interface BoardTaskRepository extends JpaRepository<BoardTask, Long> {

    // Магия! Тут пусто.
    // Но наследуясь от JpaRepository, ты СРАЗУ получаешь готовые методы:
    // .save(), .findById(), .findAll(), .delete() и т.д.
}