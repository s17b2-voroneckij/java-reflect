package org.example;

import java.util.Map;

public class Main {
    public static void main(String[] args) throws IllegalAccessException {
        var a = new A(1, "asdf", 2.0);
        for (var entry : JReflectUtils.toMap(a).entrySet()) {
            System.out.printf("%s %s\n", entry.getKey(), entry.getValue());
        }
        var b = new B();
        JReflectUtils.fromMapSetFields(JReflectUtils.toMap(a), b);
        System.out.println(b);

        var c = new C();
        JReflectUtils.convert(a, c, Map.of("a", "A", "b", "B", "c", "C"),
                Map.of("c", String::valueOf));
        System.out.println(c);

        b = new B();
        var converter = JReflectUtils.getConverter(a, B.class, Map.of());
        converter.accept(a, b);
        System.out.println(b);

        ReflectUtils.Converter<A, B> conv = new ReflectUtils.Converter<>(A.class, B.class, Map.of(), Map.of());
        System.out.println("now testing fast converter");
        b = new B();
        conv.convertFastNoTransform(a, b);
        System.out.println(b);
    }
}