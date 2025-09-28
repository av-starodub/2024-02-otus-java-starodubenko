package ru.otus.atmemulator.exeption;

public class NotEnoughMoneyException extends AtmException {

    public NotEnoughMoneyException(String message) {
        super(message);
    }
}
