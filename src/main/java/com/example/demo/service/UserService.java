package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public void addUser(){
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setName("test");
        user.setPasswd("passwd");
        userRepository.addUser(user);
    }
}
