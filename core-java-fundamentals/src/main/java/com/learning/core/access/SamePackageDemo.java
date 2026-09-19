package com.learning.core.access;

public class SamePackageDemo {
    
    public static void printAccessibleValues() {
        AccessControlledClass object = new AccessControlledClass();
        
        //object.privateValue; // Does not compile

        System.out.println("object.packageValue = " + object.packageValue);
        System.out.println("object.protectedValue = " + object.protectedValue);
        System.out.println("object.publicValue = " + object.publicValue);
    }

}
