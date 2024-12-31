package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Controller
public class MainController {
    @Autowired
    UserService userService;

    @GetMapping("/")
    public String get(Model model) throws SQLException {
        model.addAttribute("message", "Hello World");

        userService.addUser();

        return "/MainPage/MainPage.html";
    }

}