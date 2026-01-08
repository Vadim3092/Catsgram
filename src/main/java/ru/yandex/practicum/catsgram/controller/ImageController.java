package ru.yandex.practicum.catsgram.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.catsgram.model.Image;
import ru.yandex.practicum.catsgram.model.ImageData;
import ru.yandex.practicum.catsgram.service.ImageService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/{postId}")
public class ImageController {

    private final ImageService imageService;

    @GetMapping("/images")
    public List<Image> getPostImages(@PathVariable long postId) {
        return imageService.getPostImages(postId);
    }

    @PostMapping("/images")
    public ResponseEntity<List<Image>> uploadImages(
            @PathVariable long postId,
            @RequestParam("images") List<MultipartFile> files
    ) {
        List<Image> savedImages = imageService.saveImages(postId, files);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedImages);
    }

    @GetMapping(value = "/images/{imageId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> downloadImage(@PathVariable long imageId) {
        ImageData imageData = imageService.getImageData(imageId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename(imageData.getName())
                        .build()
        );

        return new ResponseEntity<>(imageData.getData(), headers, HttpStatus.OK);
    }
}
