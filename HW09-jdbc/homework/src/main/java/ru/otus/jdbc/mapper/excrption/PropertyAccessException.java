package ru.otus.jdbc.mapper.excrption;

public class PropertyAccessException extends RuntimeException {
    public PropertyAccessException(String message) {
        super(message);
    }

    public PropertyAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
