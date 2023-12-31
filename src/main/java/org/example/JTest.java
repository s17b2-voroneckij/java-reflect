package org.example;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.ToString;

public class JTest {
    static Gson gson = new Gson();

    @ToString
    static class A {
        public Integer a;
        public String b;
        private Double c;
        private int d;
    }

    public static void main(String[] argv) {
        System.out.println(gson.fromJson("{a: 1, b: \"str\", c: 0.6, d: 5}", A.class));

        String s = "{a: 1, b: \"str\", c: 0.6, d: 5}";
//        TypeToken<A> token = new TypeToken<>(){};
//        while (true) {
//            gson.fromJson(s, A.class);
//        }
    }
}
