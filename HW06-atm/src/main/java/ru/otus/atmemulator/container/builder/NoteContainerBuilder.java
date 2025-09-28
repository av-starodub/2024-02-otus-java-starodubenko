package ru.otus.atmemulator.container.builder;

import ru.otus.atmemulator.container.NoteContainer;

public interface NoteContainerBuilder<T extends NoteContainer> {

    T build();
}
