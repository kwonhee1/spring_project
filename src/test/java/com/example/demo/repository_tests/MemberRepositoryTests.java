package com.example.demo.repository_tests;

import com.example.demo.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@WebMvcTest
@ExtendWith(SpringExtension.class)
@Configuration
public class MemberRepositoryTests {
    @Autowired
    private MemberRepository memberRepository;

    @Test
    public void isExistEmailTest() throws Exception{
        // 준비
        String email = "test@gmail.com";

        // 실행
        Boolean result = memberRepository.isExistEmail(email);

        //확인
        Assertions.assertTrue(result);
    }
}
