package com.borda.borda_service.controller;

import com.borda.borda_service.entity.Post;
import com.borda.borda_service.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller; // <-- ВАЖНО: НЕ @RestController, а @Controller!
import org.springframework.ui.Model; // Чтобы передать данные в HTML
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller // Мы используем @Controller, чтобы можно было возвращать имена HTML страниц
@RequestMapping("/") // Теперь базовый путь просто корень сайта
public class ImageboardController {

    private final PostRepository repository;
    private static final String UPLOAD_DIR = "uploads/";

    @Autowired
    public ImageboardController(PostRepository repository) {
        this.repository = repository;
    }
    // 1.
    // ГЛАВНАЯ СТРАНИЦА: Показываем только ОП-посты (Треды)
    @GetMapping
    public String index(Model model) {
        //List<Post> threads = repository.findByThreadIdIsNull();
        List<Post> threads = repository.findByThreadIdIsNullOrderByTimestampDesc();

        // Создаем мапу для хранения счетчиков
        Map<Post, Long> threadStats = new HashMap<>();

        for (Post thread : threads) {
            // Считаем ответы для каждого треда
            long repliesCount = repository.countByThreadId(thread.getId());
            threadStats.put(thread, repliesCount);
        }

        // Передаем в HTML и список тредов, и статистику
        model.addAttribute("posts", threads);
        model.addAttribute("stats", threadStats);

        return "index";
    }
    // СТРАНИЦА ТРЕДА: Показываем тред и ответы
    @GetMapping("/thread/{id}")
    public String viewThread(@PathVariable Long id, Model model) {
        // 1. Находим сам тред (ОП)
        Post op = repository.findById(id).orElseThrow(() -> new RuntimeException("Тред не найден"));

        // 2. Находим ответы
        List<Post> replies = repository.findByThreadIdOrderByTimestampAsc(id);

        // 3. Передаем всё в HTML
        model.addAttribute("op", op);
        model.addAttribute("replies", replies);

        return "thread";
    }

    // 2. Создать пост
    @PostMapping
    public String createPost(@RequestParam(value = "file", required = false) MultipartFile file, // required = false значит картинка не обязательна
                             @RequestParam("content") String content,
                             @RequestParam(value = "threadId", required = false) Long threadId, // ID треда, если пишем внутри треда
                             @RequestParam(value = "replyTo", required = false) Long replyTo) { // ID поста, если отвечаем

        String uniqueFileName = null;

        // Если картинка есть - сохраняем
        if (file != null && !file.isEmpty()) {
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            uniqueFileName = UUID.randomUUID() + extension;

            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            try {
                Path filePath = Paths.get(UPLOAD_DIR + uniqueFileName);
                file.transferTo(filePath);
            } catch (IOException e) {
                e.printStackTrace(); // В реальном проекте надо обрабатывать ошибку лучше
            }
        }

        Post post = new Post();
        post.setContent(content);
        post.setImageName(uniqueFileName); // Если uniqueFileName null, в базу запишется null. Это ок.
        post.setTimestamp(LocalDateTime.now());

        // Пока логика простая: если threadId не пришел, значит это нулл (новый тред)
        post.setThreadId(threadId);
        post.setReplyTo(replyTo);

        repository.save(post);

        // ЛОГИКА БАМПА
        // постили - обновляем время public.posts.timestamp у "шапки" треда
        if (threadId != null) {
            // Находим ОП-пост
            Post parentThread = repository.findById(threadId)
                    .orElseThrow(() -> new RuntimeException("Тред не найден"));

            // Ставим ему текущее время (как у ответа)
            parentThread.setTimestamp(LocalDateTime.now());

            // Сохраняем изменения родителя
            repository.save(parentThread);

            // Возвращаемся в тред
            return "redirect:/thread/" + threadId;
        }

        // Иначе (если это новый тред) - просто на главную
        return "redirect:/";
    }

    // 3. Отдать картинку (Оставляем как RestController, поэтому добавим @ResponseBody)
    // Так как у нас @RestController заменен на @Controller, для JSON/Resource нужна эта аннотация
    @GetMapping("/api/posts/image/{fileName}")
    @ResponseBody
    public ResponseEntity<Resource> getImage(@PathVariable String fileName) throws IOException {
        Path filePath = Paths.get(UPLOAD_DIR + fileName);
        Resource resource = new UrlResource(filePath.toUri());

        if (resource.exists() || resource.isReadable()) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}