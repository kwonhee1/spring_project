package com.example.demo.controller_tests;

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
public class MainControllerTest {
    @Autowired
    private MockMvc mvc;

    @Test
    public void getMethod() throws Exception {
        mvc.perform(get(""))
                .andExpect(status().isOk())
                .andExpect(view().name("/MainPage/MainPage.html"));
    }
}
