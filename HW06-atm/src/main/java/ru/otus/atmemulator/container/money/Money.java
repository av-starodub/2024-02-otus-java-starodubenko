package ru.otus.atmemulator.container.money;

import java.util.Deque;
import java.util.Map;
import ru.otus.atmemulator.container.AbstractNoteContainer;
import ru.otus.atmemulator.container.NoteContainer;
import ru.otus.atmemulator.container.builder.NoteBuilder;
import ru.otus.atmemulator.denomination.Note;

/**
 * Represents a container for a collection of banknotes encapsulated within the {@link NoteContainer}.
 * This class provides the functionality to manage banknotes and compute their total value.
 * It extends the {@link AbstractNoteContainer}, inheriting its core behavior for handling the banknotes.
 *
 * <h2>Behavior:</h2>
 * - Computes the total monetary value of the contained banknotes upon initialization.
 * - Utilizes a builder pattern through the {@link NoteBuilder} to facilitate the creation
 * of Money instances with specified banknotes.
 * <p>
 * This class is immutable and thread-safe as its fields are final and no state changes after initialization.
 */
public class Money extends AbstractNoteContainer {

    private final int amount;

    private Money(Map<Note, Deque<Note>> banknotes) {
        super(banknotes);
        amount = computeAmount(this.banknotes);
    }

    public static NoteBuilder<Money> builder() {
        return new NoteBuilder<>(Money::new);
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "Money{amount=%d, banknotes=%s}".formatted(amount, banknotes);
    }
}
