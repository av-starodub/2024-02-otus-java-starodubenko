package ru.otus.atmemulator.strategy;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import ru.otus.atmemulator.denomination.Note;

/**
 * Enum representing different strategies for dispensing notes in an ATM system. Each strategy defines
 * a specific way to order and dispense notes from the available stock.
 * <p>
 * The {@code MINIMUM_NOTES} strategy is implemented to minimize the number of dispensed notes by
 * ordering them in descending order of nominal values.
 * <p>
 * The strategies use an internal caching mechanism for performance optimization. The cache is an LRU
 * (Least Recently Used) cache, ensuring that the most recently accessed results are retained while
 * evicting older ones when the cache size limit is exceeded.
 */
public enum NoteDispenseStrategy {
    MINIMUM_NOTES((nominal1, nominal2) -> nominal2.getNominalValue() - nominal1.getNominalValue());

    private final Comparator<Note> noteComparator;

    private final Map<Key, List<Note>> lruCache;

    private static final int MAX_CACHE_SIZE = 16;

    NoteDispenseStrategy(Comparator<Note> comparator) {
        noteComparator = comparator;
        lruCache = Collections.synchronizedMap(new LinkedHashMap<>(MAX_CACHE_SIZE, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Key, List<Note>> eldest) {
                return size() > MAX_CACHE_SIZE;
            }
        });
    }

    public List<Note> getOrderedNotes(Set<Note> notes) {
        if (notes == null || notes.isEmpty()) {
            return List.of();
        }
        final Key key = Key.of(notes);
        return lruCache.computeIfAbsent(key, k -> {
            var ordered = notes.stream().sorted(noteComparator).toList();
            return List.copyOf(ordered);
        });
    }

    /**
     * Represents a unique key for caching note dispense strategies based on the set of provided notes.
     * The key is generated from the sorted nominal values of the notes, ensuring that the order and
     * duplicates of notes do not affect its uniqueness.
     */
    private static final class Key {

        private final int[] nominals;

        private final int hash;

        private Key(int[] nominals) {
            this.nominals = nominals;
            this.hash = Arrays.hashCode(nominals);
        }

        static Key of(Set<Note> notes) {
            var sortedNominalValues =
                    notes.stream().mapToInt(Note::getNominalValue).sorted().toArray();
            return new Key(sortedNominalValues);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Key other)) {
                return false;
            }
            return Arrays.equals(this.nominals, other.nominals);
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }
}
