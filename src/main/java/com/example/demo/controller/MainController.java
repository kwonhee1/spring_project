package com.example.demo.controller;

import com.example.demo.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.sql.SQLException;

@Controller
public class MainController {
    @Autowired
    MemberService userService;

    @GetMapping("/")
    public String get(Model model) throws SQLException {
        model.addAttribute("message", "Hello World");

        userService.addMmber();

        return "/MainPage/MainPage.html";
    }

}