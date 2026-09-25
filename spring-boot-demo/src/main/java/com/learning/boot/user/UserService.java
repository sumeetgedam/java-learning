package com.learning.boot.user;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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

    @Transactional(readOnly = true)
    @Cacheable(
            value = "users",
            key = "#id",
            unless="#result == null"
    )
    public UserResponse findById(long id) {
        User user =
                userRepository.findById(id)
                        .orElseThrow();

        return toResponse(user);
    }

    @Transactional
    @CachePut(
            value = "users",
            key = "#id"
    )
    public UserResponse updateEmail(
            long id,
            String email
    ) {
        User user = userRepository.findById(id)
                .orElseThrow();

        user.changeEmail(email);
        return toResponse(userRepository.save(user));
    }

    @Transactional
    @CacheEvict(
            value = "users",
            key = "#id"
    )
    public void delete(long id) {
        userRepository.deleteById(id);
    }

    private UserResponse toResponse(User user){
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}
