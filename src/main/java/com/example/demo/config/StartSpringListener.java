package com.example.demo.config;

import com.example.demo.model.Member;
import com.example.demo.model.MyModel;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartSpringListener {
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationPrepared(ApplicationReadyEvent event) {
        System.out.println("Application prepared -> create model getters each model");

        MyModel.createMap(new Member(), Member.getters);

    }
}
