package com.example.demo.config.security.util.jwt.model;

import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.config.security.authentication.CustomAuthentication;
import com.example.demo.config.security.util.jwt.exception.ValidateFailException;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public class AccessToken extends Token<Integer>{
    public static final String ACCESS = "access_token";
    public static final int ACCESS_TIME = 60 * 60 * 1000; // 1시간

    private String email;
    private String ip;
    private String agent;
    private Date expiration;
    private Collection<String> authorities;

    // 암호화
    public AccessToken(CustomAuthentication authentication, String ip, String agent) {
        super((int)authentication.getPrincipal());
        this.email = authentication.getEmail();
        this.authorities = authentication.getRoles();
        this.ip = ip;
        this.agent = agent;
        this.expiration = new Date(System.currentTimeMillis() + ACCESS_TIME);
    }

    @Override
    protected JWTCreator.Builder addCliams(JWTCreator.Builder builder) {
        return builder.withSubject(ACCESS)
                .withClaim("id", id)
                .withClaim("email", email)
                .withClaim("authorities", (List<String>)authorities)
                .withClaim("ip", ip)
                .withClaim("agent", agent)
                .withExpiresAt(expiration);
    }

    // 복구화
    public AccessToken(){}
    @Override
    protected void validate(DecodedJWT decodedJWT) {
        if( !(decodedJWT.getClaim("ip").equals(ip) && decodedJWT.getClaim("agent").equals(agent)) ){
            throw new ValidateFailException(
                String.format("token(%s,%s), request(%s,%s)",
                        decodedJWT.getClaim("ip").asString(),
                        decodedJWT.getClaim("agent").asString() , ip, agent).toString()
            );
        }
    }

    @Override
    protected CustomAuthentication getAuthentication(DecodedJWT decodedJWT) {
        return new CustomAuthentication(
                decodedJWT.getClaim("id").asInt(),
                decodedJWT.getClaim("email").asString(),
                decodedJWT.getClaim("authorities").asList(String.class));
    }
}
