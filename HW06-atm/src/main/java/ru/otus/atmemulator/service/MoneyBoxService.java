package ru.otus.atmemulator.service;

import static java.util.Objects.requireNonNull;

import ru.otus.atmemulator.container.NoteBox;
import ru.otus.atmemulator.container.NoteContainer;
import ru.otus.atmemulator.exeption.NotEnoughBanknotesException;
import ru.otus.atmemulator.exeption.NotEnoughMoneyException;
import ru.otus.atmemulator.exeption.NotFreeSpaceException;
import ru.otus.atmemulator.exeption.NotValidSumException;
import ru.otus.atmemulator.strategy.NoteDispenseStrategy;
import ru.otus.atmemulator.util.NoteRequestBuilder;

public class MoneyBoxService implements NoteBoxService {

    private final NoteBox moneyBox;

    public MoneyBoxService(NoteBox noteBox) {
        requireNonNull(noteBox, "Parameter noteBox must not be null");
        moneyBox = noteBox;
    }

    @Override
    public int putMoney(NoteContainer money) {
        requireNonNull(money, "Parameter money must not be null");

        var notesToAdd = money.getNumberOfNotes();
        var notesInStock = moneyBox.getNumberOfNotes();
        var ceilSize = moneyBox.getCeilSize();

        notesInStock.forEach((note, stackSize) -> {
            var stackSizeToAdd = notesToAdd.getOrDefault(note, 0);
            if (ceilSize - stackSize < stackSizeToAdd) {
                throw new NotFreeSpaceException("Not enough free space");
            }
        });
        return moneyBox.putNotes(money);
    }

    @Override
    public NoteContainer getMoney(int requiredSum, NoteDispenseStrategy dispenseStrategy) {
        checkSum(requiredSum, moneyBox.getAmount());
        var notesInStock = moneyBox.getNumberOfNotes();
        var notesRequired = NoteRequestBuilder.buildNoteRequestToIssue(notesInStock, requiredSum, dispenseStrategy)
                .orElseThrow(() -> new NotEnoughBanknotesException("Not enough banknotes"));
        return moneyBox.extractNotes(notesRequired);
    }

    @Override
    public int checkBalance() {
        return moneyBox.getAmount();
    }

    private void checkSum(int requiredSum, int balance) {
        var minNominal = moneyBox.getMinNominal();
        if (requiredSum < minNominal || requiredSum % minNominal != 0) {
            throw new NotValidSumException("The amount must be a multiple " + minNominal);
        }
        if (requiredSum > balance) {
            throw new NotEnoughMoneyException("Not enough money");
        }
    }
}
