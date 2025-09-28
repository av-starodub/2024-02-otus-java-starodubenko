package ru.otus.atmemulator.exeption;

public class NotEnoughBanknotesException extends AtmException {

    public NotEnoughBanknotesException(String message) {
        super(message);
    }
}
