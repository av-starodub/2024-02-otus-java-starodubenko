package ru.otus.jdbc.mapper.excrption;

public class EntityClassMetaDataException extends RuntimeException {

    public EntityClassMetaDataException(Exception ex) {
        super(ex);
    }

    public EntityClassMetaDataException(String message) {
        super(message);
    }
}
