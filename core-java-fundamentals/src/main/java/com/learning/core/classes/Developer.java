package com.learning.core.classes;

public class Developer extends Employee {

    public Developer(int id, String name) {
        super(id, name);
    }

    @Override
    public void work() {
        System.out.println(getName() + " is developing software.");
    }

    public void writeCode() {
        System.out.println(getName() + " is writing code.");
    }

    public static void main(String[] args) {
        Developer developer = new Developer(4, "Alex");

        developer.work();
        developer.writeCode();

        Employee employee = new Developer(1, "Jordan");
        employee.work();

        Employee first = new Employee(2, "Amy");
        Employee second = new Developer(3, "Zoe");

        first.work();
        second.work();

    }
}