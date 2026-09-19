package com.learning.core.annotations;

public class CommandService {

    @Command(name = "start")
    public void start() {
        System.out.println("Started");
    }

    @Command(name = "stop")
    public void stop() {
        System.out.println("Stopped");
    }

    public void internalMethod() {
        System.out.println("Internal");
    }
}
