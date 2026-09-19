package com.learning.core.objects;

import com.learning.core.classes.Product;

import java.util.*;

public class ObjectMethodsDemo {

    record Employee(int id, String name, double salary){

    }

    public static void main(String[] args) {
        List<Product> productList = new ArrayList<>();

        productList.add(new Product("Book", 20.0));
        productList.add(new Product("Laptop", 1000.0));
        productList.add(new Product("Pen", 2.0));

        Collections.sort(productList);
        System.out.println("productList = " + productList);

        productList.sort(Comparator.comparing(Product::getName));
        System.out.println("productList = " + productList);

        productList.sort(Comparator.comparing(Product::getPrice).reversed());
        System.out.println("productList = " + productList);

        productList.sort(Comparator.comparing(
                Product::getName
        ).thenComparing(Product::getPrice));
        System.out.println("productList = " + productList);

        Employee first = new Employee(1, "Alex", 80_000);
        Employee second = new Employee(1, "Alex", 80_000);
        System.out.println("second==first = " + (second==first));
        System.out.println("second.equals(first) = " + second.equals(first));
        System.out.println("first,hashCode = " + first.hashCode());
        System.out.println(first);


    }
}
