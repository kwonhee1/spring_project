package com.example.demo.controller;

import com.example.demo.exception.http.view.CustomMessage;
import com.example.demo.exception.reflection.NotExistMethodName;
import com.example.demo.model.Member;
import com.example.demo.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Controller
public class MemberController extends MyController{

    private final ObjectMapper objectMapper;

    public MemberController(ObjectMapper objectMapper) {
        super();
        this.objectMapper = objectMapper;
    }

    // Login  : uri : Login, method : get, post
    @GetMapping("/Login")
    public String loginGet() {
        return "/MemberController/LoginPage.html";
    }

    // do login and return Http Status, token
    @PostMapping("/Login")
    public ResponseEntity<?> postLogin(@RequestBody Member user) throws NotExistMethodName {
        //check input parameter
        checkInputParameter(new String[]{"email", "passwd"}, user);

        return ResponseEntity.status(HttpStatus.OK).body(String.format(CustomMessage.RETURN_SUCCESS_FORMAT.format, "login success"));
    }


    // Register : uri : Register, method : get, post,
    @GetMapping
    public String registerGet() {
        return "/MemberController/RegisterPage.html";
    }

    @PostMapping
    ResponseEntity<?> postRegister(@RequestBody Map<String, Object> reqeustMap, @Autowired MemberService service) throws NotExistMethodName {
        Member member = objectMapper.convertValue(reqeustMap.get("member"), Member.class);
        String emailKey = (String)reqeustMap.get("emailKey");

        checkInputParameter(new String[]{"email", "passwd", "name"}, member);

        service.addMember(member, emailKey);

        return ResponseEntity.status(HttpStatus.OK).body(String.format(CustomMessage.RETURN_SUCCESS_FORMAT.format, "register success"));
    }
}
