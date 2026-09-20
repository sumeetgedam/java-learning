package com.learning.boot.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class UserService {

    public void sendWelcomeEmail(
            @NotBlank String name,
            @Email String email
    ) {
        System.out.println("Sending welcome email to " + email);
    }
}
