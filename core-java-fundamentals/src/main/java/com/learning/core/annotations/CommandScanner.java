package com.learning.core.annotations;


import java.lang.reflect.Method;

public class CommandScanner {

    public static void main(String[] args) throws Exception{

        CommandService service = new CommandService();

        for(Method method : CommandService.class.getDeclaredMethods()) {
            if(method.isAnnotationPresent(Command.class)) {
                Command command = method.getAnnotation(Command.class);

                System.out.println("Command : " + command.name());

                method.invoke(service);
            }
        }
    }
}
