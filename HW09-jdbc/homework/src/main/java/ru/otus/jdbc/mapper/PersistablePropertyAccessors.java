package ru.otus.jdbc.mapper;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import ru.otus.jdbc.mapper.excrption.PropertyAccessException;

public final class PersistablePropertyAccessors {

    private final Method idGetter;

    private final List<Method> gettersWithoutId;

    private PersistablePropertyAccessors(Method idGetter, List<Method> gettersWithoutId) {
        this.idGetter = idGetter;
        this.gettersWithoutId = List.copyOf(gettersWithoutId);
    }

    public Method idGetter() {
        return idGetter;
    }

    public List<Method> gettersWithoutId() {
        return gettersWithoutId;
    }

    public static PersistablePropertyAccessors forClass(EntityClassMetaData<?> meta) {
        var readers = readersByName(meta);
        var idName = meta.getIdField().getName();

        var idGetter = Optional.ofNullable(readers.get(idName))
                .orElseThrow(() -> new PropertyAccessException("No public getter for id field: " + idName));

        var nonIdFieldAccessors = meta.getFieldsWithoutId().stream()
                .map(f -> Optional.ofNullable(readers.get(f.getName()))
                        .orElseThrow(() -> new PropertyAccessException("No public getter for field: " + f.getName())))
                .toList();

        return new PersistablePropertyAccessors(idGetter, nonIdFieldAccessors);
    }

    private static Map<String, Method> readersByName(EntityClassMetaData<?> meta) {
        try {
            var beanInfo = Introspector.getBeanInfo(meta.getConstructor().getDeclaringClass());
            return Arrays.stream(beanInfo.getPropertyDescriptors())
                    .filter(pd -> pd.getReadMethod() != null)
                    .collect(Collectors.toMap(PropertyDescriptor::getName, PropertyDescriptor::getReadMethod));
        } catch (IntrospectionException e) {
            throw new PropertyAccessException("Failed to introspect bean properties for " + meta.getName(), e);
        }
    }
}
