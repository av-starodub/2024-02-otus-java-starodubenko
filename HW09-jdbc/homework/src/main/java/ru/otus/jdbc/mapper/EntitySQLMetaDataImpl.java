package ru.otus.jdbc.mapper;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EntitySQLMetaDataImpl<T> implements EntitySQLMetaData {

    private final String selectAllSql;

    private final String selectByIdSql;

    private final String insertSql;

    private final String updateSql;

    private record Meta(
            String table,
            String idColumn,
            String allColumns,
            String columnsWithoutId,
            String valuePlaceholders,
            String updateSetClause) {}

    public EntitySQLMetaDataImpl(EntityClassMetaData<T> entityClassMetaData) {
        var meta = buildMeta(entityClassMetaData);
        selectAllSql = buildSql("select %s from %s", meta.allColumns(), meta.table());
        selectByIdSql = buildSql("select %s from %s where %s = ?", meta.allColumns(), meta.table(), meta.idColumn());
        insertSql = buildSql(
                "insert into %s(%s) values (%s)", meta.table(), meta.columnsWithoutId(), meta.valuePlaceholders());
        updateSql = buildSql("update %s set %s where %s = ?", meta.table(), meta.updateSetClause(), meta.idColumn());
    }

    @Override
    public String getSelectAllSql() {
        return selectAllSql;
    }

    @Override
    public String getSelectByIdSql() {
        return selectByIdSql;
    }

    @Override
    public String getInsertSql() {
        return insertSql;
    }

    @Override
    public String getUpdateSql() {
        return updateSql;
    }

    private Meta buildMeta(EntityClassMetaData<T> metaData) {
        var table = metaData.getName();
        var idColumn = metaData.getIdField().getName();
        var allColumns = joinColumns(metaData.getAllFields(), Field::getName);
        var fieldsWithoutId = metaData.getFieldsWithoutId();
        var columnsWithoutId = joinColumns(fieldsWithoutId, Field::getName);
        var valuePlaceholders = buildValuePlaceholders(fieldsWithoutId.size());
        var updateColumns = joinColumns(fieldsWithoutId, f -> String.format("%s = ?", f.getName()));

        return new Meta(table, idColumn, allColumns, columnsWithoutId, valuePlaceholders, updateColumns);
    }

    private String buildValuePlaceholders(int count) {
        return String.join(", ", Collections.nCopies(count, "?"));
    }

    private String joinColumns(List<Field> fields, Function<Field, String> formatter) {
        return fields.stream().map(formatter).collect(Collectors.joining(", "));
    }

    private String buildSql(String sql, Object... params) {
        return String.format(sql, params);
    }
}
