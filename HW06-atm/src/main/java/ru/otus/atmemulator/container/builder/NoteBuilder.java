package ru.otus.atmemulator.container.builder;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import ru.otus.atmemulator.container.NoteContainer;
import ru.otus.atmemulator.denomination.Note;

public final class NoteBuilder<T extends NoteContainer> implements NoteContainerBuilder<T> {

    private final Map<Note, Deque<Note>> banknotes;

    private final Function<Map<Note, Deque<Note>>, T> factory;

    public NoteBuilder(Function<Map<Note, Deque<Note>>, T> factory) {
        banknotes = new HashMap<>();
        this.factory = factory;
    }

    public NoteBuilder<T> put(Note nominal, int numberOfNotes) {
        banknotes.merge(nominal, collectNotes(nominal, numberOfNotes), (existing, add) -> {
            existing.addAll(add);
            return existing;
        });
        return this;
    }

    public NoteBuilder<T> putAll(Map<Note, Integer> notes) {
        if (notes == null || notes.isEmpty()) {
            return this;
        }
        notes.forEach((nominal, count) -> put(nominal, count == null ? 0 : count));
        return this;
    }

    @Override
    public T build() {
        return factory.apply(banknotes);
    }

    public static Deque<Note> collectNotes(Note note, int numberOfNotes) {
        var stack = new ArrayDeque<Note>();
        for (int i = 0; i < numberOfNotes; i++) {
            stack.addLast(note);
        }
        return stack;
    }
}
