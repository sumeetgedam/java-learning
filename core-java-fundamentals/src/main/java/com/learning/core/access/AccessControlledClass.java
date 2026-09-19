package com.learning.core.access;

public class AccessControlledClass {

    private String privateValue = "private";
    String packageValue = "package-private";
    protected String protectedValue = "protected";
    public String publicValue = "public";

    public void printFromSameClass() {
        System.out.println("privateValue = " + privateValue);
        System.out.println("packageValue = " + packageValue);
        System.out.println("protectedValue = " + protectedValue);
        System.out.println("publicValue = " + publicValue);
    }

}
