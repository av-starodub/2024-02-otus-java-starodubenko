package ru.otus.atmemulator.container.moneybox;

import java.util.Deque;
import java.util.Map;
import java.util.stream.IntStream;
import ru.otus.atmemulator.container.AbstractNoteContainer;
import ru.otus.atmemulator.container.NoteBox;
import ru.otus.atmemulator.container.NoteContainer;
import ru.otus.atmemulator.container.builder.NoteBuilder;
import ru.otus.atmemulator.container.money.Money;
import ru.otus.atmemulator.denomination.Note;

/**
 * The MoneyBox class represents a container designed to store and manage banknotes.
 * It extends the {@link  AbstractNoteContainer} class and implements the {@code NoteBox} interface,
 * providing storage and operations for handling a collection of banknotes while enforcing certain constraints.
 * <p>
 * The MoneyBox includes features such as
 * - Limited storage capacity defined by a ceiling size.
 * - The ability to store, extract, and calculate amounts from the contained notes.
 * - A minimum nominal value of stored banknotes automatically derived from the collection.
 * <p>
 * It is instantiated using an internal {@link  NoteBuilder} to ensure proper initialization
 * of the banknotes and constraints.
 */
public class MoneyBox extends AbstractNoteContainer implements NoteBox {

    private final int ceilSize;

    private int amount;

    private final int minNominal;

    private MoneyBox(Map<Note, Deque<Note>> banknotes, int ceilSize) {
        super(banknotes);
        amount = computeAmount(this.banknotes);
        this.ceilSize = ceilSize;
        minNominal = banknotes.keySet().stream()
                .mapToInt(Note::getNominalValue)
                .min()
                .orElse(0);
    }

    public static NoteBuilder<MoneyBox> builder(int cSize) {
        return new NoteBuilder<>(banknotes -> new MoneyBox(banknotes, cSize));
    }

    @Override
    public int putNotes(NoteContainer money) {
        var notesToAdd = money.getNumberOfNotes();
        notesToAdd.forEach((note, number) -> {
            var stackToAdd = NoteBuilder.collectNotes(note, number);
            this.banknotes.merge(note, stackToAdd, this::addStack);
            amount += note.getNominalValue() * number;
        });
        return amount;
    }

    @Override
    public NoteContainer extractNotes(Map<Note, Integer> request) {
        var builder = Money.builder();
        request.forEach((note, required) -> {
            var toExtract = required == null ? 0 : required;
            if (toExtract > 0) {
                var extracted = extract(note, toExtract);
                builder.put(note, extracted);
            }
        });
        return builder.build();
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public int getCeilSize() {
        return ceilSize;
    }

    @Override
    public int getMinNominal() {
        return minNominal;
    }

    private Deque<Note> addStack(Deque<Note> stackIn, Deque<Note> stackToAdd) {
        stackIn.addAll(stackToAdd);
        return stackIn;
    }

    private int extract(Note note, int numberOfNotes) {
        var stackIn = banknotes.get(note);
        IntStream.rangeClosed(1, numberOfNotes).forEach(extraction -> {
            stackIn.pollLast();
            amount -= note.getNominalValue();
        });
        return numberOfNotes;
    }
}
