package ru.otus.atmemulator.container;

import java.util.Map;
import ru.otus.atmemulator.denomination.Note;

public interface NoteContainer {

    int getAmount();

    /**
     * @return must return an unmodifiable map
     */
    Map<Note, Integer> getNumberOfNotes();
}
