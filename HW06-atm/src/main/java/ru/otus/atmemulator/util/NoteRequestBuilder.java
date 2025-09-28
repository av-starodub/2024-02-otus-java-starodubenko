package ru.otus.atmemulator.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import ru.otus.atmemulator.denomination.Note;
import ru.otus.atmemulator.strategy.NoteDispenseStrategy;

/**
 * Utility class for building note-dispensing requests based on the available notes in stock
 * and a required monetary amount. This class is particularly useful for financial systems
 * such as ATMs to determine how to fulfill a requested sum using available notes.
 * <p>
 * The logic considers a specific dispensing strategy to determine the order of note selection.
 * It ensures that the notes issued satisfy the required sum while adhering to the constraints
 * of available inventory. If the requested sum cannot be achieved with the given stock and strategy,
 * an empty result is returned.
 * <p>
 * The class is designed to be immutable and cannot be instantiated.
 */
public final class NoteRequestBuilder {

    private NoteRequestBuilder() {}

    public static Optional<Map<Note, Integer>> buildNoteRequestToIssue(
            Map<Note, Integer> notesInStock, int requiredSum, NoteDispenseStrategy dispenseStrategy) {
        var notesRequired = new HashMap<Note, Integer>();
        var residualAmount = requiredSum;

        if (residualAmount == 0) {
            return Optional.of(Map.of());
        }

        for (var note : dispenseStrategy.getOrderedNotes(notesInStock.keySet())) {
            var currentNominalValue = note.getNominalValue();
            var currentNominalNotesInStock = notesInStock.getOrDefault(note, 0);
            var currentNominalNotesRequired = residualAmount / currentNominalValue;
            var notesToIssue = currentNominalNotesInStock > currentNominalNotesRequired
                    ? currentNominalNotesRequired
                    : currentNominalNotesInStock;
            notesRequired.put(note, notesToIssue);
            residualAmount -= notesToIssue * currentNominalValue;
            if (residualAmount == 0) {
                break;
            }
        }
        return residualAmount == 0 ? Optional.of(notesRequired) : Optional.empty();
    }
}
