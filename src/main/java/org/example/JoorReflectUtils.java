package org.example;

import org.apache.commons.math3.util.Pair;
import org.joor.Reflect;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.joor.Reflect.*;


public class JoorReflectUtils {
    public static Map<String, Object> toMap(Object a) {
        return on(a)
                .fields()
                .entrySet()
                .stream()
                .map(entry -> {
                    var name = entry.getKey();
                    var field = entry.getValue();
                    return new Pair<String, Object>(name, field.get());
                })
                .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
    }

    public static void fromMapSetFields(Map<String, Object> map, Object a) {
        on(a)
                .fields()
                .keySet()
                .stream()
                .filter(map::containsKey)
                .forEach(name -> on(a).set(name, map.get(name)));
    }

    public static void convert(Object from, Object to, Map<String, String> renaming, Map<String, Function<Object, Object>> converters) {
        var to_fields = on(to)
                .fields()
                .keySet();
        on(from)
                .fields()
                .keySet()
                .stream()
                .filter(name -> to_fields.contains(renaming.getOrDefault(name, name)))
                .forEach(name -> {
                    var obj = on(from).get(name);
                    if (converters.containsKey(name)) {
                        obj = converters.get(name).apply(obj);
                    }
                    on(to).set(renaming.getOrDefault(name, name), obj);
                });
    }

    public static <From, To> BiConsumer<From, To> getConverter(From from_obj, Class<To> to_clazz, Map<String, String> renaming) {
        var from_name = from_obj.getClass().getCanonicalName();
        var to_name = to_clazz.getCanonicalName();
        StringBuilder code = new StringBuilder();
        code.append(String.format(
                """
                class Converter implements java.util.function.BiConsumer<%1$s, %2$s> {
                    public void accept(%1$s from, %2$s to) {
                """, from_name, to_name
        ));
        on(from_obj)
                .fields()
                .keySet()
                .forEach(name -> code.append(String.format("to.%1$s = from.%1$s;\n", renaming.getOrDefault(name, name), name)));
        code.append("}}");
        return Reflect.compile("Converter", code.toString()).create().get();
    }
}
