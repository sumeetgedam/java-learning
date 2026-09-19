package com.learning.core.types;

public class House {

    private String address;

    public House(String address) {
        this.address = address;
    }

    public class Room {
        public void printAddress() {
            System.out.println(address);
        }
    }

    public static void main(String[] args) {
        House house = new House("Wall Street");

        House.Room room = house.new Room();
        room.printAddress();
    }
}
