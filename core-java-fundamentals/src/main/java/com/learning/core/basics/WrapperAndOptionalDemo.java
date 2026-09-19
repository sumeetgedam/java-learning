package com.learning.core.basics;

import javax.swing.text.html.Option;
import java.util.Objects;
import java.util.Optional;

public class WrapperAndOptionalDemo {

    static Optional<String> findUserName() {
        return Optional.empty();
    }

    public static void main(String[] args) {
        int primitive = 10;
        Integer wrapper = primitive;
        System.out.println(wrapper);

        Integer wrapper_1 = 10;
        int primitive_1 = wrapper_1;
        System.out.println(primitive_1);

        Integer value = null;
        // causes NullPointerException
        // int number  = value;

        int number = value != null ? value : 0;

        Integer first = 1000;
        Integer second = 2000;
        System.out.println(first == second);
        System.out.println(first.equals(second));
        System.out.println(Objects.equals(first, second));

        int num = Integer.parseInt("43");
        long largeNum = Long.parseLong("15555555");

        String text = Integer.toString(23);

        System.out.println(Integer.MAX_VALUE);
        System.out.println(Integer.MIN_VALUE);
        System.out.println(Integer.compare(10, 20));

        // NumberFormatException
        // int val = Integer.parseInt("adc");

        Optional<String> name = Optional.of("Alex");
        Optional<String> empty = Optional.empty();
        Optional<String> nullable = Optional.ofNullable(null);

        if(name.isPresent()) {
            System.out.println(name.get());
        }

        name.ifPresent(v -> System.out.println("Name : " + v));

        String result = empty.orElse("Unkown");
        String r = empty.orElseGet(() -> "Generated default");



        Optional<Integer> length = name.map(String::length);
        System.out.println(length);

        Optional<String> longName = name.filter(
                v -> v.length() > 3
        );

        Optional<String> normalized = name.flatMap(
                v -> Optional.of(v.toUpperCase())
        );

        String resultEx = empty.orElseThrow(
                ()->new IllegalStateException("Name not found")
        );

    }
}
