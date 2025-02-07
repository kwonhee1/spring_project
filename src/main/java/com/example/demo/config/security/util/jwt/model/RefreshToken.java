package com.example.demo.config.security.util.jwt.model;

import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.config.security.authentication.CustomAuthentication;

public class RefreshToken extends Token<Integer>{
    public static final String REFRESH = "refresh_token";
    public static final int REFRESH_TIME = Integer.MAX_VALUE; // 1시간

    private String email;
    private int level;

    // 암호화
    public RefreshToken(CustomAuthentication authentication) {
        super((int)authentication.getPrincipal());
        this.email = authentication.getEmail();
        this.level = authentication.getRefreshLevel();
    }

    @Override
    protected JWTCreator.Builder addCliams(JWTCreator.Builder builder) {
        return builder.withSubject(REFRESH)
                .withClaim("id", id)
                .withClaim("email", email)
                .withClaim("level", level);
    }

    // 복구화
    public RefreshToken(){}
    @Override
    protected void validate(DecodedJWT decodedJWT) {;}

    @Override
    protected CustomAuthentication getAuthentication(DecodedJWT decodedJWT) {
        return new CustomAuthentication(
                decodedJWT.getClaim("id").asInt(),
                decodedJWT.getClaim("level").asInt());
    }
}
