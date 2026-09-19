package com.learning.core.classes;

import java.util.Objects;

public class Employee {

    private final int id;
    private final String name;

    public Employee(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }
    public  String getName() {
        return name;
    }

    @Override
    public boolean equals(Object object) {
        if(this == object) return true;
        if(!(object instanceof Employee other)) return false;

        return id == other.id && Objects.equals(name, other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
    public void work() {
        System.out.println(name + " is working");
    }
}