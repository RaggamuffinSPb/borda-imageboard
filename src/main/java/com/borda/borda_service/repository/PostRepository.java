package com.borda.borda_service.repository;

import com.borda.borda_service.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    long countByThreadId(Long threadId);

    // 1. Найти все треды (где threadId пустой)
    // Spring сам сгенерирует SQL: SELECT * FROM posts WHERE thread_id IS NULL
    List<Post> findByThreadIdIsNull(); // deprecated, снизу метод с бампами по таймштампу
    List<Post> findByThreadIdIsNullOrderByTimestampDesc();

    // 2. Найти все ответы для конкретного треда + сортировка по времени
    // SQL: SELECT * FROM posts WHERE thread_id = ? ORDER BY timestamp ASC
    List<Post> findByThreadIdOrderByTimestampAsc(Long threadId);
}