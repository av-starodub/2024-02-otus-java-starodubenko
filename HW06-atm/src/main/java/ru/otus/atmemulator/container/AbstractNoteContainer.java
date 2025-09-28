package ru.otus.atmemulator.container;

import java.util.*;
import java.util.stream.Collectors;
import ru.otus.atmemulator.denomination.Note;

/**
 * An abstract implementation of the {@link NoteContainer} interface. This class is designed to provide
 * common functionality for handling a collection of banknotes represented as a mapping of {@link Note}
 * objects to stacks of {@link Note} instances.
 * <p>
 * Subclasses are expected to extend this class to implement specific behavior while inheriting base
 * functionality for managing and computing properties related to the contained banknotes.
 */
public abstract class AbstractNoteContainer implements NoteContainer {

    protected final Map<Note, Deque<Note>> banknotes;

    protected AbstractNoteContainer(Map<Note, Deque<Note>> banknotes) {
        Objects.requireNonNull(banknotes, "Parameter banknotes must not be null");
        this.banknotes = new HashMap<>(banknotes);
    }

    @Override
    public Map<Note, Integer> getNumberOfNotes() {
        return banknotes.entrySet().stream().collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, e -> e.getValue()
                .size()));
    }

    protected static int computeAmount(Map<Note, Deque<Note>> banknotes) {
        return banknotes.entrySet().stream()
                .mapToInt(entry ->
                        entry.getKey().getNominalValue() * entry.getValue().size())
                .sum();
    }
}
