package com.example.demo.controller;

import com.example.demo.config.security.authentication.CustomAuthentication;
import com.example.demo.exception.http.view.CustomMessage;
import com.example.demo.exception.reflection.NotExistMethodName;
import com.example.demo.model.Member;
import com.example.demo.service.MemberService;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Controller
public class MemberController {
    private static final Log log = LogFactory.getLog(MemberController.class);
    @Autowired
    private MemberService service;

    // UserPage
    @GetMapping(URIMappers.UserPageURI)
    public String userPage(Model model, ServletRequest request, HttpServletResponse response) {
        CustomAuthentication authentication = (CustomAuthentication) SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication);
        model.addAttribute("email", authentication.getEmail());

        return URIMappers.UserPageHtml;
    }

    // Login  : uri : Login, method : get, post
    @GetMapping("/Login")
    public String loginGet() {
        return "/MemberController/LoginPage.html";
    }

    // Register : uri : Register, method : get, post,
    @GetMapping("/Register")
    public String registerGet() {
        return "/MemberController/RegisterPage.html";
    }

    // register
    @PostMapping("/Register")
    ResponseEntity<?> postRegister(@RequestBody Map<String, String> reqeustMap) throws NotExistMethodName {
        Member member = new Member().setEmail(reqeustMap.get("email")).setPasswd(reqeustMap.get("passwd")).setName(reqeustMap.get("name"));
        String emailKey = reqeustMap.get("key");

        member.checkNecessary(new String[]{"email", "passwd", "name"}, Member.getters);
        service.addMember(member, emailKey);

        return ResponseEntity.status(HttpStatus.OK).body(String.format(CustomMessage.RETURN_SUCCESS_FORMAT.format, "register success"));
    }

    // send Email
    @PutMapping("/Register")
    ResponseEntity<?> putRegister(@RequestBody Map<String, String> reqeustMap) throws NotExistMethodName {
        String email = reqeustMap.get("email");

        service.sendEmail(email);

        return ResponseEntity.status(HttpStatus.OK).body(String.format(CustomMessage.RETURN_SUCCESS_FORMAT.format, "check email"));
    }
}
