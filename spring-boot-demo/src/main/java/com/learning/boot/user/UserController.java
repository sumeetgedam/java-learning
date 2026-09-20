package com.learning.boot.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping
    public List<User> findAll(
            @RequestParam(
                    name = "name",
                    required = false
            ) String name
    ) {
        if(name == null) {
            return List.of(
                    new User(
                            1L,
                            "Alex",
                            "alex@example.com"
                    )
            );
        }

        return List.of(
                new User(
                        1L,
                        name,
                        name + "@example.com"
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findById(
            @PathVariable
            @Positive(message = "ID must be positive")
            long id
    ) {
        if(id != 1L) {
            throw new UserNotFoundException(id);
        }

        return ResponseEntity.ok(
                new User(
                        1L,
                        "Alex",
                        "alex@example.com"
                )
        );
    }

    @PostMapping
    public ResponseEntity<User> create(
            @Valid @RequestBody CreateUserRequest request
    ) {
        User user = new User(
                2L,
                request.name(),
                request.email()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
}
