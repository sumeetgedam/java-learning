package com.learning.core.access.external;

import com.learning.core.access.AccessControlledClass;

public class DifferentPackageSubclass extends AccessControlledClass {

    public void printAccessibleValues() {
//        privateValue: inaccessible
//        packageValue: inaccessible

        System.out.println("protectedValue = " + protectedValue);
        System.out.println("publicValue = " + publicValue);
    }
}
