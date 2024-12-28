package com.example.demo.controller;

import com.example.demo.Exception.HttpException.NoNecessayException;
import com.example.demo.Exception.NotExistMethodName;
import com.example.demo.model.User;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class LoginController extends MyController{
    @GetMapping("/Login")
    public String getLogin() {
        return "/LoginPage/LoginPage.html";
    }

    // do login and return Http Status, token
    @PostMapping("/Login")
    public ResponseEntity<?> login(@RequestBody User user){
        //check input parameter
        checkInputParameter(new String[]{"id", "passwd"}, user);

        System.out.println(user.toString());
        return ResponseEntity.status(HttpStatus.OK).body("login success");
    }
}
