package ru.otus.jdbc.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import ru.otus.core.repository.DataTemplate;
import ru.otus.core.repository.DataTemplateException;
import ru.otus.core.repository.executor.DbExecutor;

/**
 * Сохраняет объект в базу, читает объект из базы
 */
public class DataTemplateJdbc<T> implements DataTemplate<T> {

    private final DbExecutor dbExecutor;

    private final EntitySQLMetaData entitySQLMetaData;

    private final EntityClassMetaData<T> entityClassMetaData;

    private final PersistablePropertyAccessors propertyAccessors;

    public DataTemplateJdbc(
            DbExecutor dbExecutor, EntitySQLMetaData entitySQLMetaData, EntityClassMetaData<T> entityClassMetaData) {
        this.dbExecutor = dbExecutor;
        this.entitySQLMetaData = entitySQLMetaData;
        this.entityClassMetaData = entityClassMetaData;
        propertyAccessors = PersistablePropertyAccessors.forClass(entityClassMetaData);
    }

    @Override
    public Optional<T> findById(Connection connection, long id) {
        return dbExecutor.executeSelect(connection, entitySQLMetaData.getSelectByIdSql(), List.of(id), rs -> {
            try {
                if (rs.next()) {
                    return createEntity(rs);
                }
                return null;
            } catch (Exception e) {
                throw new DataTemplateException(e);
            }
        });
    }

    @Override
    public List<T> findAll(Connection connection) {
        return dbExecutor
                .executeSelect(connection, entitySQLMetaData.getSelectAllSql(), Collections.emptyList(), rs -> {
                    var entityList = new ArrayList<T>();
                    try {
                        while (rs.next()) {
                            entityList.add(createEntity(rs));
                        }
                        return entityList;
                    } catch (Exception e) {
                        throw new DataTemplateException(e);
                    }
                })
                .orElseThrow(() -> new DataTemplateException("Unexpected error"));
    }

    @Override
    public long insert(Connection connection, T entity) {
        try {
            var fieldValues = collectFieldValues(entity);
            return dbExecutor.executeStatement(connection, entitySQLMetaData.getInsertSql(), fieldValues);
        } catch (Exception e) {
            throw new DataTemplateException(e);
        }
    }

    @Override
    public void update(Connection connection, T entity) {
        try {
            var params = new ArrayList<>(collectFieldValues(entity));
            var idGetter = propertyAccessors.idGetter();
            params.add(idGetter.invoke(entity));
            dbExecutor.executeStatement(connection, entitySQLMetaData.getUpdateSql(), params);
        } catch (Exception e) {
            throw new DataTemplateException(e);
        }
    }

    private Object getColumn(ResultSet rs, Field field) {
        try {
            return rs.getObject(field.getName());
        } catch (SQLException e) {
            throw new DataTemplateException(e);
        }
    }

    private List<Object> collectFieldValues(T entity) {
        try {
            var fieldValues = new ArrayList<>();
            for (var getter : propertyAccessors.gettersWithoutId()) {
                fieldValues.add(getter.invoke(entity));
            }
            return fieldValues;
        } catch (Exception e) {
            throw new DataTemplateException(e);
        }
    }

    private T createEntity(ResultSet rs)
            throws InvocationTargetException, InstantiationException, IllegalAccessException {
        var fields = entityClassMetaData.getAllFields();
        var entityFieldValues =
                fields.stream().map(field -> getColumn(rs, field)).toArray();
        var entityConstructor = entityClassMetaData.getConstructor();
        return entityConstructor.newInstance(entityFieldValues);
    }
}
