package com.learning.core.types;

import java.util.ArrayList;
import java.util.List;

public class AdvancedTypesDemo {

    public enum OrderStatus {
        CREATED,
        PAID,
        SHIPPED,
        DELIVERED,
        CANCELLED
    }

    public enum Priority {
        LOW(1),
        MEDIUM(2),
        HIGH(3);

        private final int level;
        Priority(int level) {
            this.level = level;
        }

        public int getLevel() {
            return level;
        }
    }

    public record EmployeeSummary(
            int id,
            String name,
            String department
    ){
        public EmployeeSummary{
            if(id <= 0) {
                throw new IllegalArgumentException("ID mus be positive");
            }

            if(name == null || name.isBlank()) {
                throw new IllegalArgumentException("Name must not be blank");
            }
        }

    }

    public record Team(String name, List<String> members) {

        public Team {
            members = List.copyOf(members);
        }
    }

    public static void main(String[] args) {
        OrderStatus status = OrderStatus.PAID;
        if(status == OrderStatus.PAID) {
            System.out.println("Payment completed");
        }

        for(OrderStatus value : OrderStatus.values()) {
            System.out.println(value);
        }

        OrderStatus shipped = OrderStatus.valueOf("SHIPPED");

        System.out.println(Priority.HIGH.getLevel());

        EmployeeSummary employeeSummary = new EmployeeSummary(101, "Alex", "Engineering");
        System.out.println("employeeSummary.id() = " + employeeSummary.id());
        System.out.println("employeeSummary.name() = " + employeeSummary.name());
        System.out.println("employeeSummary.department() = " + employeeSummary.department());

        List<String> members = new ArrayList<>();
        Team team = new Team("Platform", members);
        members.add("Alex");
    }
}
