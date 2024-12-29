package com.example.demo.controllerTests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest
@ExtendWith(SpringExtension.class)
public class LoginControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getMethod() throws Exception {
        mockMvc.perform(get("/Login"))
                .andExpect(status().isOk())
                .andExpect(view().name("/LoginPage/LoginPage.html"));
    }
}
