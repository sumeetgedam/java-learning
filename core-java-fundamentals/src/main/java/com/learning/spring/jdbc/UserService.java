package com.learning.spring.jdbc;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private  final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void register(String name, String email) {
        userRepository.save(name, email);
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return  userRepository.findAll();
    }
}
