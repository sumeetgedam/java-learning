package com.learning.core.interfaces;

public class Service implements Loggable, Auditable {

    @Override
    public void log() {
        System.out.println("Logging");
    }

    @Override
    public void audit() {
        System.out.println("Auditing");
    }

    public static void main(String[] args) {
        Service service = new Service();
        service.log();
        service.audit();
    }
}