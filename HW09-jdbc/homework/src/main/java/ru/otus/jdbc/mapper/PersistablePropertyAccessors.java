package ru.otus.jdbc.mapper;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import ru.otus.jdbc.mapper.excrption.PropertyAccessException;

public final class PersistablePropertyAccessors {

    private final Method idGetter;

    private final Method idSetter;

    private final List<Method> gettersWithoutId;

    private final Map<Field, Method> settersByFieldWithoutId;

    private PersistablePropertyAccessors(
            Method idGetter,
            Method idSetter,
            List<Method> gettersWithoutId,
            Map<Field, Method> settersByFieldWithoutId) {
        this.idGetter = idGetter;
        this.idSetter = idSetter;
        this.gettersWithoutId = List.copyOf(gettersWithoutId);
        this.settersByFieldWithoutId = Map.copyOf(settersByFieldWithoutId);
    }

    public Method idGetter() {
        return idGetter;
    }

    public List<Method> gettersWithoutId() {
        return gettersWithoutId;
    }

    public Method idSetter() {
        return idSetter;
    }

    public Map<Field, Method> settersByFieldWithoutId() {
        return settersByFieldWithoutId;
    }

    public static PersistablePropertyAccessors forClass(EntityClassMetaData<?> meta) {
        var readers = readersByName(meta);
        var writers = writersByName(meta);

        var idName = meta.getIdField().getName();
        var idGetter = Optional.ofNullable(readers.get(idName))
                .orElseThrow(() -> new PropertyAccessException("No public getter for id field: " + idName));
        var idSetter = Optional.ofNullable(writers.get(idName))
                .orElseThrow(() -> new PropertyAccessException("No public setter for id field: " + idName));

        var gettersWithoutId = meta.getFieldsWithoutId().stream()
                .map(f -> Optional.ofNullable(readers.get(f.getName()))
                        .orElseThrow(() -> new PropertyAccessException("No public getter for field: " + f.getName())))
                .toList();

        var settersByFieldWithoutId = new LinkedHashMap<Field, Method>();
        for (var field : meta.getFieldsWithoutId()) {
            var name = field.getName();
            var setter = Optional.ofNullable(writers.get(name))
                    .orElseThrow(() -> new PropertyAccessException("No public setter for field: " + name));
            settersByFieldWithoutId.put(field, setter);
        }

        return new PersistablePropertyAccessors(idGetter, idSetter, gettersWithoutId, settersByFieldWithoutId);
    }

    private static Map<String, Method> readersByName(EntityClassMetaData<?> meta) {
        return extractPropertyAccessors(meta, PropertyDescriptor::getReadMethod);
    }

    private static Map<String, Method> writersByName(EntityClassMetaData<?> meta) {
        return extractPropertyAccessors(meta, PropertyDescriptor::getWriteMethod);
    }

    private static Map<String, Method> extractPropertyAccessors(
            EntityClassMetaData<?> meta, Function<PropertyDescriptor, Method> extractor) {
        try {
            var accessorsByName = new HashMap<String, Method>();

            var beanInfo = Introspector.getBeanInfo(meta.getConstructor().getDeclaringClass());
            for (var pd : beanInfo.getPropertyDescriptors()) {
                var accessor = extractor.apply(pd);
                if (accessor != null) {
                    accessorsByName.put(pd.getName(), accessor);
                }
            }

            return accessorsByName;

        } catch (IntrospectionException e) {
            throw new PropertyAccessException("Failed to introspect bean properties for " + meta.getName(), e);
        }
    }
}
