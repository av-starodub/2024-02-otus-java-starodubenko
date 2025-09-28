package ru.otus.atmemulator.exeption;

public class AtmException extends RuntimeException {

    public AtmException(String message, Exception e) {
        super(message, e);
    }

    public AtmException(String message) {
        this(message, null);
    }
}
