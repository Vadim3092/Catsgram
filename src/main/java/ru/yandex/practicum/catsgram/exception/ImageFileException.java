package ru.yandex.practicum.catsgram.exception;

public class ImageFileException extends RuntimeException {

    public ImageFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImageFileException(String message) {
        super(message);
    }
}
