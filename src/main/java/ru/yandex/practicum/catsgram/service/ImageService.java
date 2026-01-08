package ru.yandex.practicum.catsgram.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.catsgram.dal.ImageRepository;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.ImageFileException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Image;
import ru.yandex.practicum.catsgram.model.ImageData;
import ru.yandex.practicum.catsgram.model.Post;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final PostService postService;
    private final ImageRepository imageRepository; // ← новый

    @Value("${catsgram.image-directory}")
    private String imageDirectory;

    public List<Image> getPostImages(long postId) {
        return imageRepository.findByPostId(postId); // ← из БД
    }

    public List<Image> saveImages(long postId, List<MultipartFile> files) {
        return files.stream()
                .map(file -> saveImage(postId, file))
                .collect(Collectors.toList());
    }

    private Image saveImage(long postId, MultipartFile file) {
        Post post = postService.findPostById(postId)
                .orElseThrow(() -> new ConditionsNotMetException("Указанный пост не найден"));

        Path filePath = saveFile(file, post);

        Image image = new Image();
        image.setFilePath(filePath.toString());
        image.setPostId(postId);
        image.setOriginalFileName(file.getOriginalFilename());

        return imageRepository.save(image); // ← сохранили в БД
    }

    private Path saveFile(MultipartFile file, Post post) {
        try {
            String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
            String uniqueFileName = Instant.now().toEpochMilli() + "." + (extension != null ? extension : "bin");

            Path uploadPath = Paths.get(imageDirectory, String.valueOf(post.getAuthorId()), String.valueOf(post.getId()));
            Path filePath = uploadPath.resolve(uniqueFileName);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            file.transferTo(filePath);
            return filePath;
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить файл изображения", e);
        }
    }

    public ImageData getImageData(long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new NotFoundException("Изображение с id = " + imageId + " не найдено"));
        byte[] data = loadFile(image);
        return new ImageData(data, image.getOriginalFileName());
    }

    private byte[] loadFile(Image image) {
        Path path = Paths.get(image.getFilePath());
        if (Files.exists(path)) {
            try {
                return Files.readAllBytes(path);
            } catch (IOException e) {
                throw new ImageFileException("Ошибка чтения файла. Id: " + image.getId()
                        + ", name: " + image.getOriginalFileName(), e);
            }
        } else {
            throw new ImageFileException("Файл не найден. Id: " + image.getId()
                    + ", name: " + image.getOriginalFileName());
        }
    }
}
