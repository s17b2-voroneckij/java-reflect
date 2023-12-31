package org.example;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ReflectUtils {
    public static Map<String, Object> toMap(Object a) throws IllegalAccessException {
        Class<?> clazz = a.getClass();
        var fields = clazz.getDeclaredFields();
        Map<String, Object> result = new HashMap<>();
        for (var field : fields) {
            field.setAccessible(true);
            result.put(field.getName(), field.get(a));
        }
        return result;
    }

    static class Converter<T, G> {
        private final Map<Field, Field> field_mapping = new HashMap<>();
        private final Map<String, Function<Object, Object>> converters;

        Converter(Class<T> clazz_from, Class<G> clazz_to, Map<String, String> field_name_mapping,
                  Map<String, Function<Object, Object>> converters) {
            this.converters = converters;
            var to_fields = Arrays.stream(clazz_to.getDeclaredFields()).collect(Collectors.toMap(
                    Field::getName, item -> item
            ));
            Arrays.stream(clazz_from.getDeclaredFields()).forEach(
                    field -> {
                        var name = field.getName();
                        if (field_name_mapping.containsKey(name)) {
                            to_fields.get(field_name_mapping.get(name)).setAccessible(true);
                            field.setAccessible(true);
                            field_mapping.put(field, to_fields.get(field_name_mapping.get(name)));
                        } else if (to_fields.containsKey(name)) {
                            to_fields.get(name).setAccessible(true);
                            field.setAccessible(true);
                            field_mapping.put(field, to_fields.get(name));
                        }
                    }
            );
        }

        public void convert(T from, G to) throws IllegalAccessException {
            for (var entry : field_mapping.entrySet()) {
                var name = entry.getKey().getName();
                if (converters.containsKey(name)) {
                    entry.getValue().set(to, converters.get(name).apply(entry.getKey().get(from)));
                } else {
                    entry.getValue().set(to, entry.getKey().get(from));
                }
            }
        }
    }
}
