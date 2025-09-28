package ru.otus.atmemulator.container;

import java.util.Map;
import ru.otus.atmemulator.denomination.Note;

public interface NoteBox extends NoteContainer {

    int putNotes(NoteContainer money);

    NoteContainer extractNotes(Map<Note, Integer> request);

    int getCeilSize();

    int getMinNominal();
}
