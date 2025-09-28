package ru.otus.atmemulator.atm;

import ru.otus.atmemulator.container.NoteContainer;
import ru.otus.atmemulator.strategy.NoteDispenseStrategy;

public interface ATM {

    int putMoney(NoteContainer money);

    NoteContainer getMoney(int requiredSum, NoteDispenseStrategy dispenseStrategy);

    int checkBalance();
}
