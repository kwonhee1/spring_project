package com.example.demo.model_tests;

import com.example.demo.model.Member;
import com.example.demo.model.MyModel;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MyModelTests {
    @Test
    public void myModelTests() {
        MyModel.createMap(new Member(), Member.getters);
        Member member = new Member();
        member.setEmail("test");
        member.setPasswd("pass");

        member.checkNecessary(new String[]{"email", "passwd"}, Member.getters);
    }
}
