package ru.otus.atmemulator.denomination;

public final class Note {

    private final int nominal;

    private Note(int nominal) {
        if (nominal <= 0) {
            throw new IllegalArgumentException("Parameter nominal must be > 0");
        }
        this.nominal = nominal;
    }

    public static Note of(int nominal) {
        return new Note(nominal);
    }

    public int getNominalValue() {
        return nominal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Note note)) {
            return false;
        }
        return nominal == note.nominal;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(nominal);
    }

    @Override
    public String toString() {
        return "Note{nominal=%d}".formatted(nominal);
    }
}
