package ru.otus.atmemulator.service;

import ru.otus.atmemulator.container.NoteContainer;
import ru.otus.atmemulator.strategy.NoteDispenseStrategy;

public interface NoteBoxService {

    int putMoney(NoteContainer money);

    NoteContainer getMoney(int sum, NoteDispenseStrategy strategy);

    int checkBalance();
}
