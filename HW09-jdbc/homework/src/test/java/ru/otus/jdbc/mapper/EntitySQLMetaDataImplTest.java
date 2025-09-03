package ru.otus.jdbc.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.lang.reflect.Field;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Manager;

class EntitySQLMetaDataImplTest {

    private EntitySQLMetaData entitySQLMetaDataClient;

    private EntitySQLMetaData entitySQLMetaDataManager;

    private EntityClassMetaData<Client> entityClassMetaDataClient;

    @BeforeEach
    void init() {
        entityClassMetaDataClient = new EntityClassMetaDataImpl<>(Client.class);
        entitySQLMetaDataClient = new EntitySQLMetaDataImpl<>(entityClassMetaDataClient);
        var entityClassMetaDataManager = new EntityClassMetaDataImpl<>(Manager.class);
        entitySQLMetaDataManager = new EntitySQLMetaDataImpl<>(entityClassMetaDataManager);
    }

    @Test
    @DisplayName("should create sql 'select by id' correctly")
    void getSelectByIdSqlTest() {
        var actualSelectByIdSql = entitySQLMetaDataClient.getSelectByIdSql();
        var expectedSelectByIdSql = "select id, name from client where id = ?";
        assertThat(actualSelectByIdSql).isEqualToIgnoringCase(expectedSelectByIdSql);
    }

    @Test
    @DisplayName("should create sql 'select all' correctly")
    void getSelectAllSqlTest() {
        var actualSelectAllSql = entitySQLMetaDataClient.getSelectAllSql();
        var expectedColumns = entityClassMetaDataClient.getAllFields().stream()
                .map(Field::getName)
                .collect(Collectors.joining(", "));
        var expectedSelectAllSql = "select %s from client".formatted(expectedColumns);
        assertThat(actualSelectAllSql).isEqualToIgnoringCase(expectedSelectAllSql);
    }

    @Test
    @DisplayName("should create sql 'insert' correctly")
    void getInsertSqlTest() {
        var actualInsertSql = entitySQLMetaDataManager.getInsertSql();
        var expectedInsertSql = "insert into manager(label, param1) values (?, ?)";
        assertThat(actualInsertSql).isEqualToIgnoringCase(expectedInsertSql);
    }

    @Test
    @DisplayName("should create sql 'update' correctly")
    void getUpdateSqlTest() {
        var actualUpdateSql = entitySQLMetaDataManager.getUpdateSql();
        var expectedUpdateSql = "update manager set label = ?, param1 = ? where no = ?";
        assertThat(actualUpdateSql).isEqualToIgnoringCase(expectedUpdateSql);
    }
}
