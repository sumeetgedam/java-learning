package com.learning.boot.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class UserOrderService {

    private final UserRepository userRepository;

    public UserOrderService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User createUserWithOrder(
            String name,
            String email,
            BigDecimal total
    ) {
        User user = new User(name, email);
        Order order = new Order(total);

        user.addOrder(order);
        return userRepository.save(user);

    }
}
