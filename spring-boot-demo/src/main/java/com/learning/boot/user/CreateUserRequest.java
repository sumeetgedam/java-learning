package com.learning.boot.user;

public record CreateUserRequest (
        String name,
        String email
) {
}
