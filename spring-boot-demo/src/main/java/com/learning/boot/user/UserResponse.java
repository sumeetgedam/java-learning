package com.learning.boot.user;

public record UserResponse(
        long id,
        String name,
        String email
) {
}
