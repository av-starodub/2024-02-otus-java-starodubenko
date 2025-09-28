package ru.otus.atmemulator.atm;

import static java.util.Objects.requireNonNull;

import ru.otus.atmemulator.container.NoteContainer;
import ru.otus.atmemulator.exeption.AtmException;
import ru.otus.atmemulator.service.NoteBoxService;
import ru.otus.atmemulator.strategy.NoteDispenseStrategy;

public class AtmEmulator implements ATM {

    private final NoteBoxService noteBoxService;

    public AtmEmulator(NoteBoxService noteBoxService) {
        requireNonNull(noteBoxService, "Parameter noteBoxService must not be null");
        this.noteBoxService = noteBoxService;
    }

    @Override
    public int putMoney(NoteContainer money) {
        requireNonNull(money, "Parameter money must not be null");
        try {
            return noteBoxService.putMoney(money);
        } catch (Exception e) {
            throw new AtmException(createErrorMessage(e.getMessage()), e);
        }
    }

    @Override
    public NoteContainer getMoney(int requiredSum, NoteDispenseStrategy dispenseStrategy) {
        try {
            return noteBoxService.getMoney(requiredSum, dispenseStrategy);
        } catch (Exception e) {
            throw new AtmException(createErrorMessage(e.getMessage()), e);
        }
    }

    @Override
    public int checkBalance() {
        return noteBoxService.checkBalance();
    }

    private String createErrorMessage(String message) {
        return (String.format("Error. Operation can be performed, %s", message));
    }
}
