package com.indyforge;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.stream.Stream;

/**
 * @author Christopher Probst <christopher.probst@hhu.de>
 * @version 1.0, 20.04.15
 */
public class DynamicTypingApp {

    @FunctionalInterface
    public interface Handler {
        void handle(Object object) throws Throwable;
    }

    public static Object call(Object receiver,
                              String method,
                              Object... args) throws Throwable {


        switch (method) {
            case "+":
                if (receiver.getClass() == String.class) {
                    return (String) receiver + args[0];
                } else {
                    return ((Number) receiver).doubleValue() + ((Number) args[0]).doubleValue();
                }
            case "-":
                return ((Number) receiver).doubleValue() - ((Number) args[0]).doubleValue();
            case "*":
                return ((Number) receiver).doubleValue() * ((Number) args[0]).doubleValue();
            case "/":
                return ((Number) receiver).doubleValue() / ((Number) args[0]).doubleValue();
            // ... process all operators
        }
        Class<?>[] argsClasses = Stream.generate(() -> Object.class)
                                       .limit(args.length)
                                       .toArray(Class<?>[]::new);
        Method methodHandle = receiver.getClass().getDeclaredMethod(method, argsClasses);
        methodHandle.setAccessible(true);
        return methodHandle.invoke(receiver, args);
    }

    public static void forEach(Object iterable, Handler handler) throws Throwable {
        for (Object element : (Iterable) iterable) {
            handler.handle(element);
        }
    }

    // DEMO STARTS HERE


    public static class Car {

        Object velocity, mass;

        public Car(Object velocity, Object mass) {
            this.velocity = velocity;
            this.mass = mass;
        }

        public Object getMass() {
            return mass;
        }

        public Object getVelocity() {
            return velocity;
        }
    }

    public static void main(String[] args) throws Throwable {

        Object autos = new ArrayList();

        call(autos, "add", new Car(100, 1000));
        call(autos, "add", new Car(120, 1000));
        call(autos, "add", new Car(130, 1000));

        // Enhanced for-each loop
        forEach(autos, auto -> {
            call(System.out, "println", call("Mass ", "+", call(auto, "getMass")));
        });

        // Manual iteration
        Object iterator = call(autos, "iterator");
        while (call(iterator, "hasNext").equals(true)) {
            Object auto = call(iterator, "next");
            call(System.out, "println", call("Mass ", "+", call(auto, "getMass")));
        }


        // REPL: Java 9 might include an own REPL
    }
}
