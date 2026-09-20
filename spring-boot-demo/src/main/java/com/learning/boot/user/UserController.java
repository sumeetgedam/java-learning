package com.learning.boot.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
            @PathVariable long id
    ) {
        if(id != 1L) {
            return ResponseEntity.notFound().build();
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
            @RequestBody CreateUserRequest request
    ) {
        User user = new User(
                2L,
                request.name(),
                request.email()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
}
