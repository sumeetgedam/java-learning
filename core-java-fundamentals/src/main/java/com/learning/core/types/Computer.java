package com.learning.core.types;

public class Computer {

    private final  String brand;

    public Computer(String brand) {
        this.brand = brand;
    }

    public static class Specs {
        private final int memoryGb;
        private final int storageGb;

        public Specs(int memoryGb, int storageGb) {
            this.memoryGb = memoryGb;
            this.storageGb = storageGb;
        }

        public int getMemoryGb() {
            return memoryGb;
        }

        public int getStorageGb() {
            return storageGb;
        }
    }

    public static void main(String[] args) {
        Computer.Specs specs = new Computer.Specs(8, 256);
    }
}
