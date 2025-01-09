package com.example.demo.utils;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;

import com.example.demo.config.security.authentication.CustomAuthentication;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Service
public class JWTService {
    private static final int ACCESS_TIME = 60 * 60 * 1000; // 1시간
    private static final String SECRET = "very_stronger_secret";
    public static final String ACCESS = "access_token";

    private JWTVerifier jwtVerifier;

    public JWTService(){
        jwtVerifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
    }

    public String createAccessToken(String email, List<String> authorities, String inputIp, String agent) {
        return JWT.create()
                .withSubject(ACCESS)
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TIME))
                .withClaim("email", email)
                .withClaim("authorities", authorities)
                .withClaim("ip", inputIp)
                .withClaim("agent", agent)
                .sign(Algorithm.HMAC256(SECRET));
    }

    public Authentication getAuthentication(String token, String ip, String agent){
        DecodedJWT decodedJWT = jwtVerifier.verify(token);

        // ip and agent 확인
        if (!decodedJWT.getClaim("ip").asString().equals(ip) || !decodedJWT.getClaim("agent").asString().equals(agent)) {
            throw new RuntimeException("JWT verification failed");
        }

        return new CustomAuthentication(decodedJWT.getClaim("email").asString(), null, decodedJWT.getClaim("authorities").asList(String.class));
    }
}
