package com.learning.boot.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User create(
            String name, String email
    ) {
        if(userRepository.existsByEmail(email)) {
            throw new IllegalStateException("Email already exists");
        }

        return userRepository.save(new User(name, email));
    }

    @Transactional(readOnly = true)
    public User findRequired(long id) {
        return userRepository
                .findById(id)
                .orElseThrow(()-> new UserNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void sendWelcomeEmail(
            @NotBlank String name,
            @Email String email
    ) {
        System.out.println("Sending welcome email to " + email);
    }
}
