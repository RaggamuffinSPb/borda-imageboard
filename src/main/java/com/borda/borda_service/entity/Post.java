package com.borda.borda_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Текст сообщения
    @Column(columnDefinition = "TEXT")
    private String content;

    // Имя файла (картинки). Теперь может быть NULL!
    private String imageName;

    // Дата и время (теперь правильный тип Java, а не строка)
    private LocalDateTime timestamp;

    // --- НОВЫЕ ПОЛЯ ДЛЯ ТРЕДОВ И ОТВЕТОВ ---

    // ID треда, к которому относится пост.
    // Если NULL — значит, это самый первый пост (ОП), то есть начало нового Треда.
    private Long threadId;

    // ID поста, на который мы отвечаем.
    // Если NULL — это просто сообщение в треде, не ответ никому конкретному.
    private Long replyTo;
}