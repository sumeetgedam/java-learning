package com.learning.boot.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserSearchService {

    private final UserRepository userRepository;

    public UserSearchService(
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    public Page<UserResponse> search(
            String name,
            String email,
            Pageable pageable
    ) {
        Specification<User> specification = Specification
                .where(
                        UserSpecifications.nameContains(name)
                )
                .and(
                        UserSpecifications.emailContains(email)
                );

        return userRepository
                .findAll(specification, pageable)
                .map(user ->
                        new UserResponse(
                                user.getId(),
                                user.getName(),
                                user.getEmail()
                        )
                );
    }
}
