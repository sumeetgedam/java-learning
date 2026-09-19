package com.learning.core.classes;

public class Circle extends Shape {

    private final double radius;

    public Circle(double radius) {
        this.radius = radius;
    }

    @Override
    public double area() {
        return Math.PI * radius * radius;
    }

    public static void main(String[] args) {
        Shape shape = new Circle(5);
        System.out.println(shape.area());
    }
}