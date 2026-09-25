package com.learning.boot.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindUserByEmail() {
        User user = new User("Alex", "alex@example.com");

        userRepository.save(user);

        Optional<User> result =
                userRepository.findByEmail("alex@example.com");

        assertThat(result)
                .isPresent()
                .get()
                .extracting(User::getName)
                .isEqualTo("Alex");
    }

    @Test
    void shouldFindByEmail() {
        User saved = userRepository.save(
                new User(
                        "Alex",
                        "alex@example.com"
                )
        );

        Optional<User> result = userRepository.findByEmail(
                "alex@example.com"
        );

        assertThat(result)
                .isPresent()
                .get()
                .extracting(User::getId)
                .isEqualTo(saved.getId());
    }
}
