package com.learning.boot.user;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(long id) {
        super("User not found: " +id);
    }
}
