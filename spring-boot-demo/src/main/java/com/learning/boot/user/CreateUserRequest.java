package com.learning.boot.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest (
        @NotBlank(message = "Name is required")
        @Size(
                min = 2,
                max = 100,
                message = "NAme must contain 2 to 100 characters"
        )
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email
) {
}
