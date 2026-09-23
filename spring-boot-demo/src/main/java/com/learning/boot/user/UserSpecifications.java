package com.learning.boot.user;

import org.springframework.data.jpa.domain.Specification;

public final class UserSpecifications {

    private UserSpecifications(){}

    public static Specification<User> nameContains(
            String name
    ) {
        return (root, query, builder) ->
                name == null || name.isBlank() ? null :
                        builder.like(
                                builder.lower(
                                        root.get("name")
                                ),
                                "%" +
                                        name.toLowerCase() + "%"
                        );
    }

    public static  Specification<User> emailContains(
            String email
    ) {
        return (root, query, builder) ->
                email == null || email.isBlank() ? null :
                        builder.like(builder.lower(
                                root.get("email")
                        ),
                                "%" +
                                email.toLowerCase() + "%"
                        );
    }
}
