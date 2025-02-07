package com.example.demo.config.security.filters;

import com.example.demo.config.security.CustomRequestMatchers;

public abstract class CustomTokenFilter extends CustomFilter {

    protected String tokenName;

    protected CustomTokenFilter(String tokenName, CustomRequestMatchers requestMatchers) {
        super(requestMatchers);
        this.tokenName = tokenName;
    }
}