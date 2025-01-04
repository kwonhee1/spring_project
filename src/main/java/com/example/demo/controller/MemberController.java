package com.example.demo.controller;

import com.example.demo.exception.http.view.CustomMessage;
import com.example.demo.exception.reflection.NotExistMethodName;
import com.example.demo.model.Member;
import com.example.demo.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Controller
public class MemberController {
    @Autowired
    private MemberService service;

    // Login  : uri : Login, method : get, post
    @GetMapping("/Login")
    public String loginGet() {
        return "/MemberController/LoginPage.html";
    }

    // do login and return Http Status, token
//    @PostMapping("/Login")
//    public ResponseEntity<?> postLogin(@RequestBody Member user) throws NotExistMethodName {
//        //check input parameter
//        user.checkNecessary(new String[]{"email", "passwd"},Member.getters);
//        return ResponseEntity.status(HttpStatus.OK).body(String.format(CustomMessage.RETURN_SUCCESS_FORMAT.format, "login success"));
//    }


    // Register : uri : Register, method : get, post,
    @GetMapping("/Register")
    public String registerGet() {
        return "/MemberController/RegisterPage.html";
    }

    @PostMapping("/Register")
    ResponseEntity<?> postRegister(@RequestBody Map<String, String> reqeustMap) throws NotExistMethodName {
        Member member = new Member().setEmail(reqeustMap.get("email")).setPasswd(reqeustMap.get("passwd")).setName(reqeustMap.get("name"));
        String emailKey = reqeustMap.get("key");

        member.checkNecessary(new String[]{"email", "passwd", "name"}, Member.getters);
        service.addMember(member, emailKey);

        return ResponseEntity.status(HttpStatus.OK).body(String.format(CustomMessage.RETURN_SUCCESS_FORMAT.format, "register success"));
    }

    @PutMapping("/Register")
    ResponseEntity<?> putRegister(@RequestBody Map<String, String> reqeustMap) throws NotExistMethodName {
        String email = reqeustMap.get("email");

        service.sendEmail(email);

        return ResponseEntity.status(HttpStatus.OK).body(String.format(CustomMessage.RETURN_SUCCESS_FORMAT.format, "check email"));
    }
}
