package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.dal.PostRepository;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Post;

import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final UserService userService;
    private final PostRepository postRepository; // ← новый

    public PostService(UserService userService, PostRepository postRepository) {
        this.userService = userService;
        this.postRepository = postRepository; // ← новый
    }

    public List<Post> findAll(int from, int size, SortOrder sortOrder) {
        if (size <= 0) {
            throw new ConditionsNotMetException("Размер страницы должен быть больше 0");
        }

        List<Post> allPosts = postRepository.findAll(); // ← из БД
        List<Post> sortedPosts = allPosts.stream()
                .sorted((sortOrder == SortOrder.DESCENDING)
                        ? Comparator.comparing(Post::getPostDate).reversed()
                        : Comparator.comparing(Post::getPostDate))
                .collect(Collectors.toList());

        if (from >= sortedPosts.size()) {
            return Collections.emptyList();
        }

        int toIndex = Math.min(from + size, sortedPosts.size());
        return sortedPosts.subList(from, toIndex);
    }

    public Post create(Post post) {
        if (post.getDescription() == null || post.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }

        Long authorId = post.getAuthorId();
        if (authorId == null) {
            throw new ConditionsNotMetException("Не указан автор поста");
        }
        if (userService.findUserById(authorId).isEmpty()) {
            throw new ConditionsNotMetException("Автор с id = " + authorId + " не найден");
        }

        post.setPostDate(Instant.now());
        return postRepository.save(post); // ← сохранили в БД
    }

    public Post update(Post newPost) {
        if (newPost.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        Post oldPost = postRepository.findById(newPost.getId())
                .orElseThrow(() -> new NotFoundException("Пост с id = " + newPost.getId() + " не найден"));
        if (newPost.getDescription() == null || newPost.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }
        oldPost.setDescription(newPost.getDescription());
        return postRepository.update(oldPost); // ← обновили в БД
    }

    public Optional<Post> findPostById(long id) {
        return postRepository.findById(id); // ← из БД
    }
}
