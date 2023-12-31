package org.example;

import org.joor.Reflect;

import java.util.function.Supplier;

public class CodeGen {
    static private final Supplier<String> supplier = Reflect.compile(
            "HelloWorld",
            """
                    class HelloWorld implements java.util.function.Supplier<String> {
                        public String get() {
                            return "Hello World!";
                        }
                    }
                    """).create().get();

    static private final Supplier<String> supplier2 = Reflect.compile(
            "HelloWorld",
            """
                    class HelloWorld implements java.util.function.Supplier<String> {
                        public String get() {
                            return "Hello World!2";
                        }
                    }
                    """).create().get();

    public static void main(String[] argv) {
        System.out.println(supplier.get());
        System.out.println(supplier2.get());
    }
}
