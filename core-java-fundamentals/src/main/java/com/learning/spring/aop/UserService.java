package com.learning.spring.aop;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Audited
    public String findUser(long id) {
        System.out.println("Finding user : " + id);
        return "User : " + id;
    }

    public void deleteUser(long id) {
        System.out.println("Deleting User : " + id);
    }
}
