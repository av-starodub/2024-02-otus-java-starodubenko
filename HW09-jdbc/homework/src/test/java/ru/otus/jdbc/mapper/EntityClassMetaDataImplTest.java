package ru.otus.jdbc.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Manager;

class EntityClassMetaDataImplTest {

    @Test
    @DisplayName("Should return class name")
    void getNameTest() {
        var entityClassMetaDataClient = new EntityClassMetaDataImpl<>(Client.class);
        var actualClassName = entityClassMetaDataClient.getName();
        assertThat(actualClassName).isEqualTo(Client.class.getSimpleName());
    }

    @Test
    @DisplayName("Should return Constructor<T> with All arguments")
    void getConstructorTest() throws NoSuchMethodException {
        var entityClassMetaDataClient = new EntityClassMetaDataImpl<>(Client.class);
        var actualConstructor = entityClassMetaDataClient.getConstructor();
        var expectedConstructor = Client.class.getConstructor();
        assertThat(actualConstructor).isEqualTo(expectedConstructor);
    }

    @Test
    @DisplayName("Should return @id annotated field")
    void getIdFieldTest() throws NoSuchFieldException {
        var entityClassMetaDataManager = new EntityClassMetaDataImpl<>(Manager.class);
        var actualId = entityClassMetaDataManager.getIdField();
        assertThat(actualId).isEqualTo(Manager.class.getDeclaredField("no"));
    }

    @Test
    @DisplayName("Should return all fields")
    void getAllFieldTest() {
        var entityClassMetaDataManager = new EntityClassMetaDataImpl<>(Manager.class);
        var actualFields = entityClassMetaDataManager.getAllFields();
        var expectedFields = Manager.class.getDeclaredFields();
        assertThat(actualFields).containsExactlyInAnyOrder(expectedFields);
    }

    @Test
    @DisplayName("Should return all fields without @d annotated")
    void getFieldsWithoutIdTest() throws NoSuchFieldException {
        var entityClassMetaDataManager = new EntityClassMetaDataImpl<>(Manager.class);
        var actualFields = entityClassMetaDataManager.getFieldsWithoutId();
        Field[] expectedFields = {Manager.class.getDeclaredField("label"), Manager.class.getDeclaredField("param1")};
        assertThat(actualFields).containsExactlyInAnyOrder(expectedFields);
    }
}
